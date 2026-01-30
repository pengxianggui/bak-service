package io.github.pengxianggui.bak.util;

import cn.hutool.core.lang.Assert;
import io.github.pengxianggui.bak.BakException;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class CommandUtil {

    /**
     * 获取bash命令
     *
     * @return
     */
    public static String getBashCommand() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.toLowerCase().contains("win") ? "C:\\Program Files\\Git\\bin\\bash.exe" : "bash";
    }

    /**
     * 执行脚本，并返回执行过程的日志
     *
     * @param env  环境变量参数
     * @param args 脚本执行参数，首个应当是bash或sh命令
     * @return 脚本执行日志
     * @throws IOException
     */
    public static List<String> executeScript(Map<String, String> env, String... args) throws IOException {
        List<String> logs = new ArrayList<>();
//        args = escapeArgs(args);
        // 构建ProcessBuilder
        ProcessBuilder processBuilder = inheritEnv(new ProcessBuilder(args), false);
        processBuilder.environment().putAll(env);
        processBuilder.redirectErrorStream(true);  // 将 stderr 合并到 stdout
        // 执行脚本
        Process process = processBuilder.start();
        // 读取标准输出流
        BufferedReader stdOutput = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        String line;
        while ((line = stdOutput.readLine()) != null) {
            log.info(line);
            logs.add(line);
        }
        int exitCode;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String finalResult = logs.isEmpty() ? "" : logs.get(logs.size() - 1);
        Assert.isTrue(exitCode == 0, () -> new BakException(logs, "脚本执行失败，退出码: {}, msg: {}", exitCode, finalResult));
        return logs;
    }

    /**
     * 参数处理
     * 处理对象: 带有单、双引号的参数项
     * 处理方式: 转义单、双引号，并将含有单双的参数项用双引号包裹起来
     * 为什么要处理：参数中带有单、双引号传入shell后会被抹去
     *
     * @param args
     * @return
     */
    private static String[] escapeArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String argI = args[i];
            if (argI == null) {
                continue;
            }
            if (argI.contains("'") || argI.contains("\"")) {
                argI = argI.replace("\"", "\\\""); // 先替换双引号
                argI = argI.replace("\'", "\\\""); // 再替换单引号
                args[i] = "\"" + argI + "\"";
            }
        }
        return args;
    }

    /**
     * 继承环境变量
     *
     * @param processBuilder
     * @param inheritIO      是否继承IO, 若true, 脚本执行的日志会管道重定向到java进程。会导致process.getInputStream拿不到内容。
     * @return
     */
    public static ProcessBuilder inheritEnv(ProcessBuilder processBuilder, boolean inheritIO) {
        Map<String, String> env = processBuilder.environment();
        String currentPath = System.getenv("PATH");

        // 补全所有可能的路径：
        // /opt/homebrew/bin (Mac M1)
        // /usr/local/mysql/bin (Mac Intel / Linux 默认)
        // /usr/bin:/bin (基础路径)
        String extraPaths = "/opt/homebrew/bin:/usr/local/opt/mysql-client/bin/:/usr/local/bin:/usr/local/mysql/bin";
        env.put("PATH", extraPaths + ":" + currentPath);
        log.debug("env:");
        for (Map.Entry<String, String> entry : env.entrySet()) {
            log.debug("{}={}", entry.getKey(), entry.getValue());
        }
        if (inheritIO) {
            processBuilder.inheritIO();
        }
        return processBuilder;
    }

    public static void main(String[] args) {
        String[] args1 = new String[]{"bash", "-c", "id=1 and type='A' and sub_type=\"a\""};
        String[] args2 = escapeArgs(args1);
        for (int i = 0; i < args2.length; i++) {
            System.out.println(args2[i]);
        }
    }
}
