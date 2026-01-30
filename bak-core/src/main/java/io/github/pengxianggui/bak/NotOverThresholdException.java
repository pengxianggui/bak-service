package io.github.pengxianggui.bak;

/**
 * 未达到归档阈值
 *
 * @author pengxg
 * @date 2026/1/30 13:57
 */
public class NotOverThresholdException extends Exception {
    public NotOverThresholdException(String message) {
        super(message);
    }
}
