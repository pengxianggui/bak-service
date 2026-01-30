package io.github.pengxianggui.bak;

import java.util.List;

/**
 * 未达到归档阈值
 *
 * @author pengxg
 * @date 2026/1/30 13:57
 */
public class NotOverThresholdException extends Exception {
    /**
     * 阈值脚本执行日志
     */
    private List<String> shellLogs;

    public NotOverThresholdException(String message, List<String> shellLogs) {
        super(message);
        this.shellLogs = shellLogs;
    }

    @Override
    public String getMessage() {
        return getAllMsg();
    }

    public String getAllMsg() {
        String message = super.getMessage();
        String shellLogStr = "";
        if (shellLogs != null) {
            shellLogStr = String.join("\n", shellLogs);
        }
        return message + "\n\n" + shellLogStr;
    }
}
