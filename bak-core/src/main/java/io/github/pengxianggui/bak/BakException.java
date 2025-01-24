package io.github.pengxianggui.bak;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author pengxg
 * @date 2025-01-06 13:32
 */
@Slf4j
public class BakException extends RuntimeException {
    @Getter
    private OprType oprType;

    public static BakException bakEx(String messageTmpl, Object... args) {
        return new BakException(messageTmpl, args).oprType(OprType.bak);
    }

    public static BakException restoreEx(String messageTmpl, Object... args) {
        return new BakException(messageTmpl, args).oprType(OprType.restore);
    }

    public static BakException archiveEx(String messageTmpl, Object... args) {
        return new BakException(messageTmpl, args).oprType(OprType.archive);
    }

    public static BakException exportEx(String messageTmpl, Object... args) {
        return new BakException(messageTmpl, args).oprType(OprType.export);
    }

    public BakException(String messageTmpl, Object... args) {
        super(String.format(messageTmpl, args));
    }

    public BakException(Throwable cause) {
        super(cause);
    }

    public BakException(Throwable cause, String messageTmpl, Object... args) {
        super(String.format(messageTmpl, args), cause);
    }

    public BakException oprType(OprType oprType) {
        this.oprType = oprType;
        return this;
    }

    private static String format(String messageTmpl, Object... args) {
        try {
            return String.format(messageTmpl, args);
        } catch (Exception e) {
            log.warn("模板参数配置有误, msg:{}", e.getMessage());
            return messageTmpl;
        }
    }
}
