package io.github.pengxianggui.bak.mysqldump;

import io.github.pengxianggui.bak.util.FileUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * dump配置类
 *
 * @author pengxg
 * @date 2025-01-06 15:59
 */
@Slf4j
public class DumpConfig {
    private static final DumpExecutor defaultDumpExecutor = new DefaultDumpExecutor();
    private static Map<String, DumpExecutor> EXECUTOR_MAP = new HashMap<>();

    // 数据库ip
    @Getter
    @Setter
    private String dbIp;
    // 数据库端口
    @Getter
    @Setter
    private int dbPort;
    // 数据库用户名
    @Getter
    @Setter
    private String dbUsername;
    // 数据库密码
    @Getter
    @Setter
    private String dbPassword;
    // 默认导出文件输出目录
    @Getter
    @Setter
    private String exportDir;
    // 默认备份文件输出目录
    @Getter
    @Setter
    private String bakDir;
    // 默认归档文件输出目录
    @Getter
    @Setter
    private String archiveDir;
    // 阈值检测脚本路径
    @Getter
    @Setter
    private String thresholdScriptPath = "classpath:shell/over_threshold.sh";
    // 备份脚本路径
    @Getter
    @Setter
    private String bakScriptPath = "classpath:shell/run_bak.sh";
    // 归档脚本路径
    @Getter
    @Setter
    private String archiveScriptPath = "classpath:shell/run_archive.sh";
    // 还原脚本路径
    @Getter
    @Setter
    private String restoreScriptPath = "classpath:shell/run_restore.sh";
    private File thresholdScript;
    private File bakScript;
    private File archiveScript;
    private File restoreScript;

    /**
     * 配置执行器。定制化执行器逻辑时调用，可针对每种数据类目配置不同的执行器
     *
     * @param code     数据类目编码
     * @param executor 执行器
     */
    public void configExecutor(String code, DumpExecutor executor) {
        EXECUTOR_MAP.put(code, executor);
    }

    public DumpExecutor getExecutor(String code) {
        DumpExecutor dumpExecutor;
        dumpExecutor = EXECUTOR_MAP.getOrDefault(code, defaultDumpExecutor);
        if (dumpExecutor.getConfig() == null) {
            dumpExecutor.config(this);
        }
        return dumpExecutor;
    }

    public String getDbPortAsStr() {
        return String.valueOf(this.getDbPort());
    }

    public File getRestoreScript() throws IOException {
        if (this.restoreScript == null) {
            this.restoreScript = FileUtils.loadFile(restoreScriptPath);
        }
        return this.restoreScript;
    }

    public File getArchiveScript() throws IOException {
        if (this.archiveScript == null) {
            this.archiveScript = FileUtils.loadFile(archiveScriptPath);
        }
        return this.archiveScript;
    }

    public File getBakScript() throws IOException {
        if (this.bakScript == null) {
            this.bakScript = FileUtils.loadFile(bakScriptPath);
        }
        return this.bakScript;
    }

    public File getThresholdScript() throws IOException {
        if (this.thresholdScript == null) {
            this.thresholdScript = FileUtils.loadFile(thresholdScriptPath);
        }
        return this.thresholdScript;
    }

    /**
     * 预加载好脚本文件。在装配好dumpConfig后，调用此方法，预先加载好脚本文件
     *
     * @return
     */
    public void loadScript() throws IOException {
        this.restoreScript = FileUtils.loadFile(restoreScriptPath);
        this.archiveScript = FileUtils.loadFile(archiveScriptPath);
        this.bakScript = FileUtils.loadFile(bakScriptPath);
        this.thresholdScript = FileUtils.loadFile(thresholdScriptPath);
    }

    public DumpConfig dbIp(String dbIp) {
        this.dbIp = dbIp;
        return this;
    }

    public DumpConfig dbPort(int dbPort) {
        this.dbPort = dbPort;
        return this;
    }

    public DumpConfig dbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
        return this;
    }

    public DumpConfig dbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
        return this;
    }

    public DumpConfig bakDir(String defaultBakDir) {
        this.bakDir = defaultBakDir;
        return this;
    }

    public DumpConfig archiveDir(String defaultArchiveDir) {
        this.archiveDir = defaultArchiveDir;
        return this;
    }

    public DumpConfig exportDir(String defaultExportDir) {
        this.exportDir = defaultExportDir;
        return this;
    }

    /**
     * 配置备份脚本路径, classpath: 开头表示classpath路径
     *
     * @param bakScriptPath
     * @return
     */
    public DumpConfig bakScriptPath(String bakScriptPath) {
        this.bakScriptPath = bakScriptPath;
        return this;
    }

    /**
     * 配置归档脚本路径, classpath: 开头表示classpath路径
     *
     * @param archiveScriptPath
     * @return
     */
    public DumpConfig archiveScriptPath(String archiveScriptPath) {
        this.archiveScriptPath = archiveScriptPath;
        return this;
    }

    /**
     * 配置还原脚本路径, classpath: 开头表示classpath路径
     *
     * @param restoreScriptPath
     * @return
     */
    public DumpConfig restoreScriptPath(String restoreScriptPath) {
        this.restoreScriptPath = restoreScriptPath;
        return this;
    }

    /**
     * 配置阈值检测脚本路径, classpath: 开头表示classpath路径
     *
     * @param thresholdScriptPath
     * @return
     */
    public DumpConfig thresholdScriptPath(String thresholdScriptPath) {
        this.thresholdScriptPath = thresholdScriptPath;
        return this;
    }
}
