package io.github.pengxianggui.bak.controller;

import io.github.pengxianggui.bak.controller.vo.Result;
import io.github.pengxianggui.crud.dynamic.Crud;
import io.github.pengxianggui.crud.dynamic.CrudService;
import io.github.pengxianggui.bak.service.TaskConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@Api(tags = "任务配置")
@RestController
@RequestMapping("taskConfig")
@Crud
public class TaskConfigController {

    @Resource
    @CrudService
    private TaskConfigService taskConfigService;

    @ApiOperation("启用任务")
    @PostMapping("enable/{id}")
    public Result<Boolean> start(@PathVariable("id") Long id) {
        boolean flag = taskConfigService.enable(id);
        return flag ? Result.success(true, "启动成功") : Result.fail(500, "启动失败");
    }

    @ApiOperation("禁用任务")
    @PostMapping("disable/{id}")
    public Result<Boolean> stop(@PathVariable("id") Long id) {
        boolean flag = taskConfigService.disable(id);
        return flag ? Result.success(true, "停止成功") : Result.fail(500, "停止失败, 请刷新页面再尝试");
    }
}
