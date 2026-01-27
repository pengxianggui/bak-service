package io.github.pengxianggui.bak.controller;

import io.github.pengxianggui.bak.controller.dto.ArchiveParam;
import io.github.pengxianggui.bak.controller.dto.BakParam;
import io.github.pengxianggui.bak.controller.vo.Result;
import io.github.pengxianggui.bak.enums.FileSuffix;
import io.github.pengxianggui.bak.service.OprLogService;
import io.github.pengxianggui.bak.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author pengxg
 * @date 2025-01-08 15:09
 */
@Slf4j
@Api(tags = "任务操作")
@RestController
@RequestMapping("task")
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private OprLogService oprLogService;

    @ApiOperation(value = "基于任务配置执行一次备份/归档", notes = "并返回生成的文件路径, 前端可调用【备份记录】>【备份/归档文件下载】接口进行下载")
    @PostMapping("run/{id}")
    public Result<String> run(@ApiParam("任务配置id") @PathVariable("id") Long taskConfigId) {
        try {
            File file = taskService.run(taskConfigId);
            return Result.success(oprLogService.getPreviewUrl(file.getAbsolutePath()), "任务执行成功, 文件已经生成");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Result.fail(500, e.getMessage());
        }

    }

    @ApiOperation(value = "基于临时输入执行一次备份", notes = "并返回生成的文件路径, 前端可调用【备份记录】>【备份/归档文件下载】接口进行下载")
    @PostMapping("bak")
    public Result<String> bak(@Valid @RequestBody BakParam param) {
        try {
            File file = taskService.bak(param);
            return Result.success(oprLogService.getPreviewUrl(file.getAbsolutePath()), "手动执行成功, 文件已经生成");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Result.fail(500, e.getMessage());
        }
    }

    @ApiOperation(value = "基于临时输入执行一次归档", notes = "并返回生成的文件路径, 前端可调用【备份记录】>【备份/归档文件下载】接口进行下载")
    @PostMapping("archive")
    public Result<String> archive(@Valid @RequestBody ArchiveParam param) {
        try {
            File file = taskService.archive(param);
            return Result.success(file.getAbsolutePath(), "手动执行成功, 文件已经生成");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Result.fail(500, "执行失败", e.getMessage());
        }
    }

    @ApiOperation(value = "基于备份记录还原数据")
    @PostMapping("restore/{logId}")
    public Result<Boolean> restore(@ApiParam("备份记录id(只有类型为bak的执行记录才能执行此操作)") @PathVariable("logId") Long logId) {
        try {
            taskService.restore(logId);
            return Result.success(true, "还原成功, 请检查数据!");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Result.fail(500, e.getMessage());
        }
    }

    @ApiOperation("按数据类目导出")
    @GetMapping("/export/{type}")
    public ResponseEntity<InputStreamResource> exportByCategory(@ApiParam("csv、sql、txt、xlsx") @PathVariable("type") FileSuffix type,
                                                                @Valid @RequestBody BakParam bakParam) throws IOException {
        File file = taskService.export(type, bakParam);
        if (file == null || !file.exists() || !file.isFile()) {
            throw new FileNotFoundException("文件不存在!"); // 文件不存在或路径不正确时返回404
        }

        // 设置文件下载的内容类型和头信息
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

        FileInputStream fileInputStream = new FileInputStream(file);
        InputStreamResource resource = new InputStreamResource(fileInputStream);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
