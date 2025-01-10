package io.github.pengxianggui.bak.service.impl;

import cn.hutool.core.lang.Assert;
import io.github.pengxianggui.crud.BaseServiceImpl;
import io.github.pengxianggui.bak.domain.TaskConfig;
import io.github.pengxianggui.bak.mapper.TaskConfigMapper;
import io.github.pengxianggui.bak.service.TaskConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TaskConfigServiceImpl extends BaseServiceImpl<TaskConfig, TaskConfigMapper> implements TaskConfigService {

    @Resource
    private TaskConfigMapper taskConfigMapper;

    @Override
    public void init() {
        this.baseMapper = taskConfigMapper;
        this.clazz = TaskConfig.class;
    }

    public boolean enable(Long id) {
        TaskConfig taskConfig = getById(id);
        Assert.notNull(taskConfig, "任务配置不存在!");
        taskConfig.setEnable(Boolean.TRUE);
        return updateById(taskConfig);
    }

    public boolean disable(Long id) {
        TaskConfig taskConfig = getById(id);
        Assert.notNull(taskConfig, "任务配置不存在!");
        taskConfig.setEnable(Boolean.FALSE);
        return updateById(taskConfig);
    }

}
