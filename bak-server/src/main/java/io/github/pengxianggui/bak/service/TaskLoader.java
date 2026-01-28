package io.github.pengxianggui.bak.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.pengxianggui.bak.TaskManager;
import io.github.pengxianggui.bak.domain.TaskConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author pengxg
 * @date 2026/1/28 15:40
 */
@Slf4j
@Component
public class TaskLoader {
    @Autowired
    private TaskConfigService taskConfigService;
    @Autowired
    private TaskManager taskManager;
    @Autowired
    private TaskService taskService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Spring容器已就绪，开始加载数据库备份/归档任务配置...");

        List<TaskConfig> configs = taskConfigService.list(
                Wrappers.lambdaQuery(TaskConfig.class).eq(TaskConfig::getEnable, Boolean.TRUE)
        );
        if (CollectionUtil.isEmpty(configs)) {
            log.info("暂无启用的任务配置..");
            return;
        }
        for (TaskConfig config : configs) {
            taskManager.start(String.valueOf(config.getId()), config.getCron(), () -> {
                try {
                    taskService.run(config.getId());
                } catch (Exception e) {
                    log.error("任务执行失败", e);
                }
            });
            log.info("任务加载成功，任务ID：{}, cron:{}", config.getId(), config.getCron());
        }
    }
}
