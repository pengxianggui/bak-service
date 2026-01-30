package io.github.pengxianggui.bak.mysqldump;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import io.github.pengxianggui.bak.ArchiveStrategyType;
import io.github.pengxianggui.bak.BakException;
import io.github.pengxianggui.bak.NotOverThresholdException;
import io.github.pengxianggui.bak.util.CommandUtil;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * dump操作核心类
 *
 * @author pengxg
 */
@Slf4j
public class DumpManager {
    private DumpConfig dumpConfig;

    public DumpManager(DumpConfig dumpConfig) {
        this.dumpConfig = dumpConfig;
    }

    /**
     * 阈值检测: 是否超阈值
     *
     * @param dbName        数据库名
     * @param tableName     表名
     * @param timeFieldName 时间字段名
     * @param strategyType  策略类型
     * @param strategyValue 策略值
     * @return 若达到阈值，则返回true
     */
    public boolean overThreshold(String dbName,
                                 String tableName,
                                 String timeFieldName,
                                 ArchiveStrategyType strategyType,
                                 int strategyValue) {
        try {
            File scriptFile = dumpConfig.getThresholdScript();
            if (scriptFile == null || !scriptFile.exists()) {
                throw new BakException("阈值检测脚本不存在! 请检查dumpConfig配置");
            }
            Map<String, String> env = new HashMap<>();
            env.put("MYSQL_PWD", dumpConfig.getDbPassword());
            List<String> logs = CommandUtil.executeScript(
                    env,
                    CommandUtil.getBashCommand(),
                    scriptFile.getAbsolutePath(),
                    dumpConfig.getDbIp(),
                    dumpConfig.getDbPortAsStr(),
                    dumpConfig.getDbUsername(),
                    dbName,
                    tableName,
                    timeFieldName,
                    strategyType.name(),
                    strategyType.getStrategyValue(strategyValue));
            Assert.isTrue(!logs.isEmpty(), "无法获取阈值检测结果");
            String resultStr = logs.get(logs.size() - 1);
            return Boolean.parseBoolean(resultStr);
        } catch (Exception e) {
            throw new BakException(e, "阈值检测脚本执行失败!");
        }
    }

    /**
     * 执行备份
     *
     * @param categoryCode   数据类目编码
     * @param dbName         库名
     * @param tableName      表名
     * @param whereCondition where条件
     * @param outputDir      输出目录, 若空则取默认配置的路径(bak.bak-dir)
     * @param zip            是否压缩
     * @return 备份/归档的文件
     * @throws IOException
     */
    public ExecuteResult<File> bak(String categoryCode,
                                   String dbName,
                                   String tableName,
                                   String whereCondition,
                                   String outputDir,
                                   boolean zip) throws IOException {
        DumpExecutor dumpExecutor = dumpConfig.getExecutor(categoryCode);
        WhereCondition condition = new WhereCondition(whereCondition);
        String outputDirPath = StrUtil.blankToDefault(outputDir, dumpConfig.getBakDir())
                + File.separator + dbName + File.separator + tableName + File.separator
                + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN);
        String outputFileName = tableName + ".sql";
        return dumpExecutor.bak(
                dumpConfig.getDbIp(),
                dumpConfig.getDbPort(),
                dumpConfig.getDbUsername(),
                dumpConfig.getDbPassword(),
                dbName,
                tableName,
                condition,
                outputDirPath,
                outputFileName,
                zip);
    }


    /**
     * 备份还原
     *
     * @param categoryCode 数据类目编码
     * @param dbName       数据库名
     * @param bakFile      备份文件
     */
    public ExecuteResult<Boolean> restore(String categoryCode,
                                          String dbName,
                                          File bakFile) throws IOException {
        DumpExecutor dumpExecutor = dumpConfig.getExecutor(categoryCode);
        return dumpExecutor.restore(
                dumpConfig.getDbIp(),
                dumpConfig.getDbPort(),
                dumpConfig.getDbUsername(),
                dumpConfig.getDbPassword(),
                dbName,
                bakFile);
    }


    /**
     * 执行归档
     *
     * @param categoryCode   数据类目编码
     * @param dbName         库名
     * @param tableName      表名
     * @param whereCondition where条件
     * @param strategyType   策略类型
     * @param strategyValue  策略值
     * @param outputDir      输出目录, 若空则取默认配置的路径(bak.archive-dir)
     * @param zip            是否压缩
     * @return 备份/归档的文件
     * @throws IOException
     */
    public ExecuteResult<File> archive(String categoryCode,
                                       String dbName,
                                       String tableName,
                                       String timeFieldName,
                                       String whereCondition,
                                       ArchiveStrategyType strategyType,
                                       Integer strategyValue,
                                       String outputDir,
                                       boolean zip) throws IOException, NotOverThresholdException {
        Assert.notBlank(timeFieldName, () -> BakException.archiveEx("归档必须提供时间字段, 请检查对应数据类目配置"));

        DumpExecutor dumpExecutor = dumpConfig.getExecutor(categoryCode);
        WhereCondition condition = new WhereCondition(whereCondition);
        Assert.isTrue(condition.getConditions().stream().noneMatch(c -> timeFieldName.endsWith(c.getField())),
                "自定义where条件( " + whereCondition + ")中应避免再使用归档策略中参考的时间字段:" + timeFieldName);
        // 阈值检测
        boolean overThreshold = overThreshold(dbName, tableName, timeFieldName, strategyType, strategyValue);
        if (!overThreshold) {
            throw new NotOverThresholdException("数据量未达到归档阈值,不执行归档操作");
        }
        String outputDirPath = StrUtil.blankToDefault(outputDir, dumpConfig.getArchiveDir())
                + File.separator + dbName + File.separator + tableName + File.separator
                + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN);
        String outputFileName = tableName + ".sql";
        return dumpExecutor.archive(
                dumpConfig.getDbIp(),
                dumpConfig.getDbPort(),
                dumpConfig.getDbUsername(),
                dumpConfig.getDbPassword(),
                dbName,
                tableName,
                timeFieldName,
                strategyType,
                strategyValue,
                condition,
                outputDirPath,
                outputFileName,
                zip);
    }


    /**
     * 导出数据
     *
     * @param categoryCode   数据类目编码, 用于定位自定义dump执行器。可空，则为默认dump执行器
     * @param dbName         数据库名
     * @param tableName      表名
     * @param whereCondition where条件。如"id>10"
     * @param outputDir      输出文件目录。若为空则取默认配置的路径(bak.export-dir)
     * @param outputFileName 输出文件名,含后缀。注意: 只支持 .sql，.txt, .csv, .xlsx
     * @param zip            是否压缩
     * @return 导出的文件
     * @throws IOException
     */
    public ExecuteResult<File> export(String categoryCode,
                                      String dbName,
                                      String tableName,
                                      String whereCondition,
                                      String outputDir,
                                      String outputFileName,
                                      boolean zip
    ) throws IOException {
        String outputDirPath = StrUtil.blankToDefault(outputDir, dumpConfig.getExportDir())
                + File.separator + dbName + File.separator + tableName + File.separator
                + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN);
        DumpExecutor dumpExecutor = dumpConfig.getExecutor(categoryCode);
        WhereCondition condition = new WhereCondition(whereCondition);
        return dumpExecutor.export(
                dumpConfig.getDbIp(),
                dumpConfig.getDbPort(),
                dumpConfig.getDbUsername(),
                dumpConfig.getDbPassword(),
                dbName,
                tableName,
                condition,
                outputDirPath,
                outputFileName,
                zip);
    }

    /**
     * 判断指定命令是否配置到环境变量中，未配置则返回true
     *
     * @return
     */
    public static boolean isCommandInvalid(String commandName) {
        try {
            // 判断操作系统
            String os = System.getProperty("os.name").toLowerCase();
            String which = os.contains("win") ? "where.exe" : "which";

            // 执行命令
            Process process = CommandUtil.inheritEnv(new ProcessBuilder(which, commandName), false).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            process.waitFor();
            return line == null || line.isEmpty() || !line.contains(commandName);
        } catch (Exception e) {
            log.error("校验{}命令是否配置到环境变量中时发生异常, msg: {}", commandName, e.getMessage());
            return true;
        }
    }

    @PostConstruct
    public void run() {
        boolean goodJob = true;
        if (isCommandInvalid("mysqldump")) {
            log.error("当前环境变量中不支持mysqldump命令，请检查是否安装并配置环境变量! 否则备份/归档功能无法使用");
            goodJob = false;
        }
        if (isCommandInvalid("mysql")) {
            log.error("当前环境变量中不支持mysql命令，请检查是否安装并配置环境变量!否则备份/归档功能无法使用");
            goodJob = false;
        }
        if (isCommandInvalid("tail")) {
            log.error("当前环境变量中不支持tail命令，请检查是否安装并配置环境变量!否则可能导致无法导出csv、txt");
            goodJob = false;
        }
        if (isCommandInvalid("sed")) {
            log.error("当前环境变量中不支持sed命令，请检查是否安装并配置环境变量!否则可能导致无法导出csv、txt");
            goodJob = false;
        }
        if (isCommandInvalid("tr")) {
            log.error("当前环境变量中不支持tr命令，请检查是否安装并配置环境变量!否则可能导致无法导出csv、txt");
            goodJob = false;
        }
        if (isCommandInvalid("soffice")) {
            log.error("当前环境变量中不支持soffice命令，请检查是否安装libreoffice并配置环境变量!否则可能导致无法导出xlsx");
            goodJob = false;
        }
        if (goodJob) {
            log.info("检测到当前环境变量已经配置mysql、mysqldump等相关命令! Good job! ");
        }
    }
}
