package io.github.pengxianggui.bak;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

/**
 * @author pengxg
 * @date 2025-01-06 13:32
 */
@Slf4j
public class BakException extends RuntimeException {
    @Getter
    private OprType oprType;
    private final List<String> shellLogs;

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
        this(Collections.emptyList(), messageTmpl, args);
    }

    public BakException(List<String> shellLogs, String messageTmpl, Object... args) {
        super(StrUtil.format(messageTmpl, args));
        this.shellLogs = shellLogs;
    }

    public BakException(Exception e, String messageTmpl, Object... args) {
        super(StrUtil.format(messageTmpl, args), e);
        this.shellLogs = (e instanceof BakException) ? ((BakException) e).shellLogs : Collections.emptyList();
    }

    public BakException oprType(OprType oprType) {
        this.oprType = oprType;
        return this;
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
