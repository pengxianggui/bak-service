package io.github.pengxianggui.bak.controller;

import io.github.pengxianggui.bak.controller.vo.Result;
import io.github.pengxianggui.bak.domain.TaskConfig;
import io.github.pengxianggui.bak.service.TaskConfigService;
import io.github.pengxianggui.crud.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "任务配置")
@RestController
@RequestMapping("taskConfig")
public class TaskConfigController extends BaseController<TaskConfig> {
    @Autowired
    private TaskConfigService taskConfigService;

    public TaskConfigController(TaskConfigService taskConfigService) {
        super(taskConfigService, TaskConfig.class);
    }

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
