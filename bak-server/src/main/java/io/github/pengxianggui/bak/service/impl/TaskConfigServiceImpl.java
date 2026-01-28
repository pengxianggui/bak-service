package io.github.pengxianggui.bak.service.impl;

import cn.hutool.core.lang.Assert;
import io.github.pengxianggui.bak.TaskManager;
import io.github.pengxianggui.bak.domain.TaskConfig;
import io.github.pengxianggui.bak.mapper.TaskConfigMapper;
import io.github.pengxianggui.bak.service.TaskConfigService;
import io.github.pengxianggui.bak.service.TaskService;
import io.github.pengxianggui.crud.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Service
public class TaskConfigServiceImpl extends BaseServiceImpl<TaskConfigMapper, TaskConfig> implements TaskConfigService {
    @Autowired
    private TaskManager taskManager;
    @Lazy
    @Autowired
    private TaskService taskService;

    @Transactional(rollbackFor = Exception.class)
    public boolean enable(Long id) {
        TaskConfig taskConfig = getById(id);
        Assert.notNull(taskConfig, "任务配置不存在!");
        taskConfig.setEnable(Boolean.TRUE);
        return updateById(taskConfig);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean disable(Long id) {
        TaskConfig taskConfig = getById(id);
        Assert.notNull(taskConfig, "任务配置不存在!");
        taskConfig.setEnable(Boolean.FALSE);
        return updateById(taskConfig);
    }

    @Override
    protected void afterUpdateById(TaskConfig entity) {
        if (entity.getEnable() == Boolean.TRUE) {
            taskManager.start(String.valueOf(entity.getId()), entity.getCron(), () -> {
                try {
                    taskService.run(entity.getId());
                } catch (IOException e) {
                    log.error("任务执行失败", e);
                }
            });
        } else {
            taskManager.stop(String.valueOf(entity.getId()));
        }
    }
}
