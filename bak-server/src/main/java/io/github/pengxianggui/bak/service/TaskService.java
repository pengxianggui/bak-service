package io.github.pengxianggui.bak.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import io.github.pengxianggui.bak.ArchiveStrategyType;
import io.github.pengxianggui.bak.UserContext;
import io.github.pengxianggui.bak.controller.dto.ArchiveParam;
import io.github.pengxianggui.bak.controller.dto.BakParam;
import io.github.pengxianggui.bak.domain.DataCategory;
import io.github.pengxianggui.bak.domain.OprLog;
import io.github.pengxianggui.bak.domain.TaskConfig;
import io.github.pengxianggui.bak.enums.FileSuffix;
import io.github.pengxianggui.bak.enums.TaskType;
import io.github.pengxianggui.bak.mysqldump.DumpManager;
import io.github.pengxianggui.bak.mysqldump.ExecuteResult;
import io.github.pengxianggui.crud.file.FileManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * @author pengxg
 * @date 2025-01-08 15:16
 */
@Slf4j
@Service
public class TaskService {
    @Autowired
    private TaskConfigService taskConfigService;
    @Autowired
    private DataCategoryService dataCategoryService;
    @Autowired
    private DumpManager dumpManager;
    @Autowired
    private OprLogService oprLogService;
    @Autowired
    private FileManager fileManager;

    public File run(Long id) throws IOException {
        TaskConfig taskConfig = taskConfigService.getById(id);
        Assert.notNull(taskConfig, "任务配置不存在!");
        TaskType taskType = TaskType.valueOf(taskConfig.getType());
        Assert.isTrue(taskType == TaskType.bak || taskType == TaskType.archive, "不支持的任务类型: %s", taskType);
        Long categoryId = taskConfig.getCategoryId();
        String outputDir = taskConfig.getPath();
        boolean zip = taskConfig.getZip();
        DataCategory category = dataCategoryService.getById(categoryId);
        Assert.notNull(category, "数据类目不存在: %d", categoryId);

        OprLog oprLog = new OprLog();
        oprLog.setOperator(StrUtil.blankToDefault(UserContext.getUserId(), "未知"));
        oprLog.setCategoryCode(category.getCode());
        oprLog.setCategoryName(category.getName());
        oprLog.setDbName(category.getDbName());
        oprLog.setTableName(category.getTableName());
        oprLog.setCond(taskConfig.getCond());
        oprLog.setType(taskType.name());

        try {
            ExecuteResult<File> result;
            if (taskType == TaskType.bak) {
                result = dumpManager.bak(category.getCode(), category.getDbName(), category.getTableName(), taskConfig.getCond(), outputDir, zip);
            } else {
                result = dumpManager.archive(category.getCode(), category.getDbName(), category.getTableName(), category.getTimeFieldName(),
                        taskConfig.getCond(), ArchiveStrategyType.valueOf(taskConfig.getStrategy()), taskConfig.getStrategyValue(),
                        outputDir, zip);
            }
            File file = result.getResult();
            Assert.isTrue(file != null && file.exists(), "生成的文件不存在!");
            String fileUrl = fileManager.getFileService().upload(file);
            oprLog.setSuccess(Boolean.TRUE);
            oprLog.setFilePath(fileUrl);
            oprLog.setMsg(result.getLogAsStr());
            oprLog.setExpiredDate(taskConfig.getKeepFate() == null ? null : DateUtil.offsetDay(new Date(), taskConfig.getKeepFate()).toLocalDateTime().toLocalDate());
            return file;
        } catch (Exception e) {
            oprLog.setSuccess(Boolean.FALSE);
            oprLog.setMsg(e.getMessage());
            throw e;
        } finally {
            try {
                oprLogService.save(oprLog);
            } catch (Exception e) {
                log.error("保存执行记录时发生异常", e);
            }
        }
    }

    public File bak(BakParam param) throws IOException {
        OprLog oprLog = new OprLog();
        String categoryCode = param.getCategoryCode();
        String categoryName = null;
        String dbName = param.getDbName();
        String tableName = param.getTableName();
        if (StrUtil.isNotBlank(categoryCode)) {
            DataCategory category = dataCategoryService.getByCode(categoryCode);
            Assert.notNull(category, "数据类目不存在: %s", categoryCode);
            categoryName = category.getName();
            dbName = StrUtil.blankToDefault(dbName, category.getDbName());
            tableName = StrUtil.blankToDefault(tableName, category.getTableName());
        }
        oprLog.setOperator(StrUtil.blankToDefault(UserContext.getUserId(), "未知"));
        oprLog.setCategoryCode(categoryCode);
        oprLog.setCategoryName(categoryName);
        oprLog.setDbName(dbName);
        oprLog.setTableName(tableName);
        oprLog.setCond(param.getCond());
        oprLog.setType(TaskType.bak.name());
        try {
            ExecuteResult<File> result = dumpManager.bak(param.getCategoryCode(), param.getDbName(), param.getTableName(), param.getCond(), null, param.getZip());
            File file = result.getResult();
            Assert.isTrue(file != null && file.exists(), "生成的文件不存在!");
            String fileUrl = fileManager.getFileService().upload(file);
            oprLog.setSuccess(Boolean.TRUE);
            oprLog.setFilePath(fileUrl);
            oprLog.setMsg(result.getLogAsStr());
            oprLog.setExpiredDate(param.getKeepFate() == null ? null : DateUtil.offsetDay(new Date(), param.getKeepFate()).toLocalDateTime().toLocalDate());
            return file;
        } catch (IOException e) {
            oprLog.setSuccess(false);
            oprLog.setMsg(e.getMessage());
            throw e;
        } finally {
            try {
                oprLogService.save(oprLog);
            } catch (Exception e) {
                log.error("保存备份记录时发生异常", e);
            }
        }
    }

    public File archive(@Valid ArchiveParam param) throws IOException {
        OprLog oprLog = new OprLog();
        String categoryCode = param.getCategoryCode();
        String categoryName = null;
        String dbName = param.getDbName();
        String tableName = param.getTableName();
        if (StrUtil.isNotBlank(categoryCode)) {
            DataCategory category = dataCategoryService.getByCode(categoryCode);
            Assert.notNull(category, "数据类目不存在: %s", categoryCode);
            categoryName = category.getName();
            dbName = StrUtil.blankToDefault(dbName, category.getDbName());
            tableName = StrUtil.blankToDefault(tableName, category.getTableName());
        }
        oprLog.setOperator(StrUtil.blankToDefault(UserContext.getUserId(), "未知"));
        oprLog.setCategoryCode(categoryCode);
        oprLog.setCategoryName(categoryName);
        oprLog.setDbName(dbName);
        oprLog.setTableName(tableName);
        oprLog.setCond(param.getCond());
        oprLog.setType(TaskType.archive.name());
        try {
            ExecuteResult<File> result = dumpManager.archive(param.getCategoryCode(), param.getDbName(), param.getTableName(), param.getTimeFieldName(), param.getCond(), param.getStrategy(), param.getStrategyValue(), null, param.getZip());
            File file = result.getResult();
            Assert.isTrue(file != null && file.exists(), "生成的文件不存在!");
            oprLog.setSuccess(Boolean.TRUE);
            oprLog.setFilePath(file.getAbsolutePath());
            oprLog.setMsg(result.getLogAsStr());
            oprLog.setExpiredDate(param.getKeepFate() == null ? null : DateUtil.offsetDay(new Date(), param.getKeepFate()).toLocalDateTime().toLocalDate());
            return file;
        } catch (IOException e) {
            oprLog.setSuccess(Boolean.FALSE);
            oprLog.setMsg(e.getMessage());
            throw e;
        } finally {
            try {
                oprLogService.save(oprLog);
            } catch (Exception e) {
                log.error("保存备份记录时发生异常", e);
            }
        }
    }

    public void restore(Long logId) throws IOException {
        OprLog oprLog = oprLogService.getById(logId);
        Assert.notNull(oprLog, () -> new IllegalArgumentException("执行记录不存在:" + logId));
        Assert.isTrue(StrUtil.equals(oprLog.getType(), TaskType.bak.name()), () -> new IllegalArgumentException("只能针对备份记录执行还原操作, 当前执行记录类型为:" + oprLog.getType()));

        OprLog newOprLog = new OprLog();
        String categoryCode = oprLog.getCategoryCode();
        String categoryName = oprLog.getCategoryName();
        String dbName = oprLog.getDbName();
        newOprLog.setOperator(StrUtil.blankToDefault(UserContext.getUserId(), "未知"));
        newOprLog.setCategoryCode(categoryCode);
        newOprLog.setCategoryName(categoryName);
        newOprLog.setDbName(dbName);
        newOprLog.setType(TaskType.restore.name());
        try {
            File file = fileManager.getFileService().getFile(oprLog.getFilePath());
            ExecuteResult<Boolean> result = dumpManager.restore(categoryCode, dbName, file);
            newOprLog.setSuccess(result.getResult());
            newOprLog.setMsg(result.getLogAsStr());
        } catch (IOException e) {
            newOprLog.setSuccess(Boolean.FALSE);
            newOprLog.setMsg(e.getMessage());
            throw e;
        } finally {
            try {
                oprLogService.save(newOprLog);
            } catch (Exception e) {
                log.error("保存备份记录时发生异常", e);
            }
        }
    }

    public File export(FileSuffix type, @Valid BakParam param) throws IOException {
        OprLog oprLog = new OprLog();
        String categoryCode = param.getCategoryCode();
        String categoryName = null;
        String dbName = param.getDbName();
        String tableName = param.getTableName();
        if (StrUtil.isNotBlank(categoryCode)) {
            DataCategory category = dataCategoryService.getByCode(categoryCode);
            Assert.notNull(category, "数据类目不存在: %s", categoryCode);
            categoryName = category.getName();
            dbName = StrUtil.blankToDefault(dbName, category.getDbName());
            tableName = StrUtil.blankToDefault(tableName, category.getTableName());
        }
        oprLog.setOperator(StrUtil.blankToDefault(UserContext.getUserId(), "未知"));
        oprLog.setCategoryCode(categoryCode);
        oprLog.setCategoryName(categoryName);
        oprLog.setDbName(dbName);
        oprLog.setTableName(tableName);
        oprLog.setCond(param.getCond());
        oprLog.setType(TaskType.export.name());
        try {
            ExecuteResult<File> result = dumpManager.export(param.getCategoryCode(), param.getDbName(), param.getTableName(), param.getCond(), null, tableName + "." + type, param.getZip());
            File file = result.getResult();
            Assert.isTrue(file != null && file.exists(), "生成的文件不存在!");
            String fileUrl = fileManager.getFileService().upload(file);
            oprLog.setSuccess(Boolean.TRUE);
            oprLog.setFilePath(fileUrl);
            oprLog.setMsg(result.getLogAsStr());
            oprLog.setExpiredDate(param.getKeepFate() == null ? null : DateUtil.offsetDay(new Date(), param.getKeepFate()).toLocalDateTime().toLocalDate());
            return file;
        } catch (Exception e) {
            oprLog.setSuccess(false);
            oprLog.setMsg(e.getMessage());
            throw e;
        } finally {
            try {
                oprLogService.save(oprLog);
            } catch (Exception e) {
                log.error("保存备份记录时发生异常", e);
            }
        }
    }

}
