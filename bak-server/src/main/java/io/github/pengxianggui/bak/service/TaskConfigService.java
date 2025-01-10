package io.github.pengxianggui.bak.service;

import io.github.pengxianggui.crud.BaseService;
import io.github.pengxianggui.bak.domain.TaskConfig;

public interface TaskConfigService extends BaseService<TaskConfig> {

    boolean enable(Long id);

    boolean disable(Long id);

}