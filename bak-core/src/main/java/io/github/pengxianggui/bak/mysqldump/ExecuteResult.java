package io.github.pengxianggui.bak.mysqldump;

import lombok.Getter;

import java.util.List;

@Getter
public class ExecuteResult<T> {
    T result;
    List<String> logs;

    public ExecuteResult(T result, List<String> logs) {
        this.result = result;
        this.logs = logs;
    }

    public String getLogAsStr() {
        if (logs != null) {
            return String.join("\n", logs);
        }
        return "";
    }
}
