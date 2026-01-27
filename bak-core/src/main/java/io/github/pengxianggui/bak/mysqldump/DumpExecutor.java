package io.github.pengxianggui.bak.mysqldump;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ZipUtil;
import io.github.pengxianggui.bak.ArchiveStrategyType;
import io.github.pengxianggui.bak.BakException;
import io.github.pengxianggui.bak.util.CommandUtil;
import io.github.pengxianggui.bak.util.FileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * dump操作执行器。默认执行器 {@link DefaultDumpExecutor}, 可以自定义，
 * 并通过{@link DumpConfig#configExecutor(String, DumpExecutor)}注册到DumpConfig中
 *
 * @author pengxg
 * @date 2025/01/06
 */
@Slf4j
public abstract class DumpExecutor {

    private DumpConfig dumpConfig;

    DumpConfig getConfig() {
        return dumpConfig;
    }

    void config(DumpConfig dumpConfig) {
        this.dumpConfig = dumpConfig;
    }

    /**
     * 导出数据
     *
     * @param dbIp           数据库IP
     * @param dbPort         数据库端口
     * @param dbUsername     数据库用户名
     * @param dbPassword     数据库密码
     * @param dbName         数据库名
     * @param tableName      表名
     * @param condition      自定义查询条件。例如: "id > 100"
     * @param outputDirPath  输出文件目录
     * @param outputFileName 输出文件名, 含后缀。注意: 只支持 .sql，.txt, .csv, .xlsx
     * @param zip            是否压缩。若压缩，导出的文件将更改为.zip
     * @return 导出的文件
     * @throws IOException
     */
    ExecuteResult<File> export(String dbIp, int dbPort, String dbUsername, String dbPassword, String dbName, String tableName,
                               WhereCondition condition, String outputDirPath, String outputFileName, boolean zip) throws IOException {
        Assert.notBlank(dbIp, () -> BakException.exportEx("数据库IP不能为空"));
        Assert.notBlank(dbUsername, () -> BakException.exportEx("数据库用户名不能为空"));
        Assert.notBlank(dbName, () -> BakException.exportEx("数据库名不能为空"));
        Assert.notBlank(tableName, () -> BakException.exportEx("表名不能为空"));
        Assert.notBlank(outputDirPath, () -> BakException.exportEx("输出目录不能为空"));
        Assert.notBlank(outputFileName, () -> BakException.exportEx("输出文件名不能为空"));
        final String regex = ".*\\.(txt|csv|sql|xlsx)$";
        Assert.isTrue(Pattern.matches(regex, outputFileName),
                () -> BakException.exportEx("不支持的文件后缀: %s, 只支持导出txt|csv|sql|xlsx", outputFileName));

        File scriptFile = dumpConfig.getBakScript();
        if (scriptFile == null || !scriptFile.exists()) {
            throw BakException.exportEx("备份/导出脚本不存在! 请检查dumpConfig配置");
        }

        File dir = new File(outputDirPath);
        if (!dir.exists()) {
            Assert.isTrue(dir.mkdirs(), () -> new BakException("无法创建目录:%s", dir.getAbsolutePath()));
        }
        String outputFilePath = outputDirPath + File.separator + outputFileName;
        List<String> logs = CommandUtil.executeScript(CommandUtil.getBashCommand(), scriptFile.getAbsolutePath(),
                dbIp, String.valueOf(dbPort), dbUsername, dbPassword, dbName, tableName, condition.toString(), outputFilePath);

        File file = new File(outputFilePath);
        if (!file.exists()) {
            throw BakException.exportEx("导出的文件(%s)不存在!", outputFilePath);
        }

        return new ExecuteResult<>(zip ? zipify(file) : file, logs);
    }

    /**
     * 备份数据
     *
     * @param dbIp           数据库IP
     * @param dbPort         数据库端口
     * @param dbUsername     数据库用户名
     * @param dbPassword     数据库密码
     * @param dbName         数据库名
     * @param tableName      表名
     * @param condition      自定义查询条件。例如: "id > 100"
     * @param outputDirPath  输出文件目录
     * @param outputFileName 输出文件名, 含后缀。注意: 只支持 .sql
     * @param zip            是否压缩。若压缩，导出的文件将更改为.zip
     */
    ExecuteResult<File> bak(String dbIp, int dbPort, String dbUsername, String dbPassword, String dbName, String tableName,
                            WhereCondition condition, String outputDirPath, String outputFileName, boolean zip) throws IOException {
        Assert.isTrue(outputFileName.endsWith(".sql"), () -> BakException.bakEx("备份文件格式不正确: %s, 只支持.sql", outputFileName));
        return export(dbIp, dbPort, dbUsername, dbPassword, dbName, tableName, condition, outputDirPath, outputFileName, zip);
    }

    /**
     * 还原数据
     *
     * @param dbIp       数据库IP
     * @param dbPort     数据库端口
     * @param dbUsername 数据库用户名
     * @param dbPassword 数据库密码
     * @param dbName     数据库名
     * @param bakFile    备份文件
     */
    ExecuteResult<Boolean> restore(String dbIp, int dbPort, String dbUsername, String dbPassword, String dbName,
                                   File bakFile) throws IOException {
        Assert.isTrue(bakFile.exists(), () -> BakException.restoreEx("备份文件不存在: %s", bakFile.getAbsolutePath()));
        String bakFilePath = FileUtil.getName(bakFile);
        Assert.isTrue(bakFilePath.endsWith(".zip") || bakFilePath.endsWith(".sql"),
                () -> BakException.restoreEx("备份文件格式不正确: %s, 只能针对.zip或.sql执行还原", bakFile.getAbsolutePath()));

        File scriptFile = dumpConfig.getRestoreScript();
        if (scriptFile == null || !scriptFile.exists()) {
            throw BakException.restoreEx("还原脚本不存在! 请检查dumpConfig配置");
        }

        List<File> executeFiles = new ArrayList<>();
        if (bakFilePath.endsWith(".zip")) {
            // 解压
            List<File> files = FileUtils.unzip(bakFile);
            Assert.isTrue(!files.isEmpty(), () -> BakException.restoreEx("zip文件解压后没有内容!请检查备份文件: %s", bakFilePath));
            for (File f : files) {
                Assert.isTrue(f.exists(), () -> BakException.restoreEx("无法执行还原！备份文件可能被删了: %s", f.getAbsolutePath()));
                Assert.isTrue(f.getAbsolutePath().endsWith(".sql"), () -> BakException.restoreEx("无法执行还原！备份文件不是sql文件: %s", f.getAbsolutePath()));
            }
            executeFiles.addAll(files);
        } else {
            executeFiles.add(bakFile);
        }

        List<String> logs = new ArrayList<>();
        for (File file : executeFiles) {
            List<String> log = CommandUtil.executeScript(CommandUtil.getBashCommand(), scriptFile.getAbsolutePath(), dbIp, String.valueOf(dbPort),
                    dbUsername, dbPassword, dbName, file.getAbsolutePath());
            logs.addAll(log);
        }
        return new ExecuteResult<>(true, logs);
    }

    /**
     * 归档数据
     *
     * @param dbIp           数据库IP
     * @param dbPort         数据库端口
     * @param dbUsername     数据库用户名
     * @param dbPassword     数据库密码
     * @param dbName         数据库名
     * @param tableName      表名
     * @param timeFieldName  时间字段名
     * @param strategy       策略类型
     * @param strategyValue  策略值
     * @param outputDirPath  输出目录
     * @param outputFileName 输出文件名, 含后缀。注意: 只支持 .sql
     * @param zip            是否压缩。若压缩，导出的文件将更改为.zip
     */
    ExecuteResult<File> archive(String dbIp, int dbPort, String dbUsername, String dbPassword, String dbName, String tableName, String timeFieldName,
                                ArchiveStrategyType strategy, Integer strategyValue, WhereCondition condition, String outputDirPath, String outputFileName, boolean zip) throws IOException {
        File scriptFile = dumpConfig.getArchiveScript();
        if (scriptFile == null || !scriptFile.exists()) {
            throw BakException.archiveEx("归档脚本不存在! 请检查dumpConfig配置");
        }

        File dir = new File(outputDirPath);
        if (!dir.exists()) {
            Assert.isTrue(dir.mkdirs(), () -> BakException.archiveEx("无法创建目录:%s", dir.getAbsolutePath()));
        }

        String outputFilePath = outputDirPath + File.separator + outputFileName;
        List<String> logs = CommandUtil.executeScript(CommandUtil.getBashCommand(), scriptFile.getAbsolutePath(), dbIp, String.valueOf(dbPort), dbUsername, dbPassword,
                dbName, tableName, timeFieldName, strategy.name(), strategy.getStrategyValue(strategyValue), condition.toString(), outputFilePath);

        File file = new File(outputFilePath);
        if (!file.exists()) {
            throw BakException.archiveEx("归档文件(%s)不存在!", outputFilePath);
        }
        return new ExecuteResult<>(zip ? zipify(file) : file, logs);
    }

    /**
     * 压缩文件。会将源文件删除
     *
     * @param file
     * @return
     */
    File zipify(File file) {
        File zipFile = ZipUtil.zip(file);
        if (!zipFile.exists()) {
            log.error("备份文件({})已经产生, 但压缩文件失败!", file.getAbsolutePath());
            return file;
        }

        log.debug("压缩文件已生成: {}, 文件大小: {}", zipFile.getAbsolutePath(), zipFile.length());
        if (!FileUtil.del(file)) {
            log.error("临时备份文件删除失败: {}", file.getAbsolutePath());
        }
        return zipFile;
    }
}
