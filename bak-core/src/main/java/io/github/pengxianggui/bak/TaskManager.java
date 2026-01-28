package io.github.pengxianggui.bak;

import cn.hutool.core.lang.Assert;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.pattern.CronPattern;
import cn.hutool.cron.task.Task;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

/**
 * 定时任务管理器
 *
 * @author pengxg
 */
@Slf4j
public class TaskManager {

    @PostConstruct
    public void init() {
        log.debug("Task manager init..");
        CronUtil.setMatchSecond(Boolean.FALSE); // 明确"分匹配", cron表达式得5位
        CronUtil.restart();
    }

    public boolean isRunning(String taskId) {
        return CronUtil.getScheduler().getTask(taskId) != null;
    }

    /**
     * 启动定时任务。幂等：对于已存在的先删除再运行, 相当于存在则重新创建
     *
     * @param taskId   任务id。调用者自行确保唯一性
     * @param cron     cron表达式
     * @param runnable 执行函数
     */
    public boolean start(String taskId, String cron, Runnable runnable) {
        Assert.notBlank(taskId);
        Assert.isTrue(validCron(cron), "cron表达式异常:{}", cron);
        log.info("Task:[{}] is starting...", taskId);
        Task task = CronUtil.getScheduler().getTask(taskId);
        // 已经在运行中，并且cron没有变化，则不做处理
        if (isRunning(taskId) && cron.equals(CronUtil.getScheduler().getPattern(taskId).toString())) {
            return true;
        }
        // 重启
        if (task != null) {
            CronUtil.remove(taskId);
        }
        CronUtil.schedule(taskId, cron, runnable::run);
        boolean flag = isRunning(taskId);
        if (flag) {
            log.info("Task:[{}] start success...", taskId);
        } else {
            log.error("Task:[{}] start failed!", taskId);
        }
        return flag;
    }

    /**
     * 启动定时任务。幂等：对于已存在的先删除再运行
     *
     * @param taskId 任务id。调用者自行确保唯一性
     */
    public boolean stop(String taskId) {
        Assert.notBlank(taskId);
        log.info("Task:[{}] is stopping...", taskId);
        if (!isRunning(taskId)) {
            log.debug("Task:[{}] has been stop, no need to do it again", taskId);
            return true;
        }
        CronUtil.remove(taskId);
        boolean flag = !isRunning(taskId);
        if (flag) {
            log.info("Task:[{}] stop success...", taskId);
        } else {
            log.info("Task:[{}] stop failed!", taskId);
        }
        return flag;
    }

    /**
     * 校验cron是否支持
     *
     * @param cron
     * @return
     */
    public boolean validCron(String cron) {
        try {
            CronPattern.of(cron);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
