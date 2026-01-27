package io.github.pengxianggui.bak.service.impl;

import cn.hutool.core.lang.Assert;
import io.github.pengxianggui.bak.domain.TaskConfig;
import io.github.pengxianggui.bak.mapper.TaskConfigMapper;
import io.github.pengxianggui.bak.service.TaskConfigService;
import io.github.pengxianggui.crud.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class TaskConfigServiceImpl extends BaseServiceImpl<TaskConfigMapper, TaskConfig> implements TaskConfigService {

    public boolean enable(Long id) {
        TaskConfig taskConfig = getById(id);
        Assert.notNull(taskConfig, "任务配置不存在!");
        taskConfig.setEnable(Boolean.TRUE);
        // TODO 提交后加入定时任务
        return updateById(taskConfig);
    }

    public boolean disable(Long id) {
        TaskConfig taskConfig = getById(id);
        Assert.notNull(taskConfig, "任务配置不存在!");
        taskConfig.setEnable(Boolean.FALSE);
        // TODO 提交后移出定时任务
        return updateById(taskConfig);
    }

}
