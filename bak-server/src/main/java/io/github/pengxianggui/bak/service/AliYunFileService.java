package io.github.pengxianggui.bak.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import io.github.pengxianggui.crud.file.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

/**
 * @author pengxg
 * @date 2026/1/28 11:18
 */
@Service
public class AliYunFileService implements FileService {
    //读取配置文件的内容
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    @Value("${aliyun.oss.keyId}")
    private String keyId;
    @Value("${aliyun.oss.keySecret}")
    private String keySecret;
    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Override
    public String getMode() {
        return "aliyun";
    }

    @Override
    public String upload(MultipartFile file, String... splitMarkers) throws IOException {
        Assert.notNull(file, "文件不能为空！");
        OSS ossClient = new OSSClientBuilder().build("https://" + this.endpoint, this.keyId, this.keySecret);
        //获取上传文件输入流
        InputStream inputStream = file.getInputStream();
        //获取文件名称
        String fileName = file.getOriginalFilename();
        //保证文件名唯一，去掉uuid中的'-'
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        fileName = DateUtil.format(new Date(), "yyyy/MM/dd/") + uuid + fileName.substring(fileName.indexOf("."));
        try {
            ossClient.putObject(this.bucketName, fileName, inputStream);
            return "https://" + this.bucketName + "." + this.endpoint + "/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("文件上传阿里云oss失败", e);
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    public String upload(File file, String... splitMarkers) throws IOException {
        Assert.notNull(file, "文件不能为空！");
        OSS ossClient = new OSSClientBuilder().build("https://" + this.endpoint, this.keyId, this.keySecret);
        //获取上传文件输入流
        InputStream inputStream = Files.newInputStream(file.toPath());
        //获取文件名称
        String fileName = FileUtil.getName(file);
        //保证文件名唯一，去掉uuid中的'-'
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        fileName = DateUtil.format(new Date(), "yyyy/MM/dd/") + uuid + fileName.substring(fileName.indexOf("."));
        try {
            ossClient.putObject(this.bucketName, fileName, inputStream);
            return "https://" + this.bucketName + "." + this.endpoint + "/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("文件上传阿里云oss失败", e);
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    public File getFile(String fileUrl) {
        if (StrUtil.isBlank(fileUrl)) {
            return null;
        }
        if (StrUtil.startWith(fileUrl, "http:") || StrUtil.startWith(fileUrl, "https:")) {
            File tempDir = FileUtil.mkdir(FileUtil.getTmpDirPath() + "/bak_" + IdUtil.fastSimpleUUID());
            return HttpUtil.downloadFileFromUrl(fileUrl, tempDir);
        } else {
            String path = Paths.get(fileUrl).toString();
            return new File(path);
        }
    }
}
