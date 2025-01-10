package io.github.pengxianggui.bak.util;

import cn.hutool.core.io.LineHandler;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ZipUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtil {


    /**
     * 将源文件内容合并到目标文件。追加到末尾
     *
     * @param targetFile  目标文件
     * @param sourceFiles 源文件
     */
    public static void merge(File targetFile, File... sourceFiles) {
        for (File sourceFile : sourceFiles) {
            // 读取源文件的每一行，并追加到目标文件
            cn.hutool.core.io.FileUtil.readLines(sourceFile, StandardCharsets.UTF_8,
                    (LineHandler) line -> cn.hutool.core.io.FileUtil.appendString(line + System.lineSeparator(), targetFile, "UTF-8"));
        }
    }

    public static boolean delete(File... file) {
        boolean result = true;
        for (File f : file) {
            if (!cn.hutool.core.io.FileUtil.del(f)) {
                result = false;
            }
        }
        return result;
    }

    public static List<File> unzip(File zipFile) {
        File dir = ZipUtil.unzip(zipFile);
        File[] subFiles = dir.listFiles();
        if (subFiles == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(subFiles);
    }

    /**
     * 若filePath是以classpath:开头，则加载类路径下的资源。否则加载系统资源
     *
     * @param filePath
     * @return
     */
    public static File loadFile(String filePath) throws IOException {
        Assert.notBlank(filePath, "filePath不能为空");
        if (filePath.startsWith("classpath:")) {
            // 类路径文件处理
            String resourcePath = filePath.substring("classpath:".length());
            InputStream inputStream = FileUtil.class.getClassLoader().getResourceAsStream(resourcePath);

            if (inputStream == null) {
                throw new IOException("Resource not found in classpath: " + resourcePath);
            }

            // 将类路径文件复制到临时文件
            File tempFile = File.createTempFile("script", ".tmp");
            try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            tempFile.deleteOnExit(); // JVM 退出时自动删除临时文件
            return tempFile;
        } else {
            // 处理绝对路径
            File file = new File(filePath);
            Assert.isTrue(file.exists() && file.isFile(), "File not found: " + filePath);
            return file;
        }
    }

    /**
     * 创建一个临时目录文件
     *
     * @return
     */
    public File getTempDir() throws IOException {
        try {
            Path tempDirPath = Files.createTempDirectory("temp");
            return tempDirPath.getParent().toFile();
        } catch (IOException e) {
            throw e;
        }
    }
}
