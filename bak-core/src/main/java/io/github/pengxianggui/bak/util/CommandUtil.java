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
     * @param args
     * @return 脚本执行日志
     * @throws IOException
     */
    public static List<String> executeScript(String... args) throws IOException {
        List<String> logs = new ArrayList<>();
        args = escapeArgs(args);
        // 构建ProcessBuilder
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        processBuilder.redirectErrorStream(true);  // 将 stderr 合并到 stdout
        // 执行脚本
        Process process = processBuilder.start();
        // 读取标准输出流
        BufferedReader stdOutput = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        String line;
        while ((line = stdOutput.readLine()) != null) {
            log.debug(line);
            logs.add(line);
        }
        int exitCode;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String finalResult = logs.isEmpty() ? "" : logs.get(logs.size() - 1);
        Assert.isTrue(exitCode == 0, () -> new BakException("脚本执行失败，退出码: %d, msg: %s", exitCode, finalResult));
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

    public static void main(String[] args) {
        String[] args1 = new String[]{"bash", "-c", "id=1 and type='A' and sub_type=\"a\""};
        String[] args2 = escapeArgs(args1);
        for (int i = 0; i < args2.length; i++) {
            System.out.println(args2[i]);
        }
    }
}
