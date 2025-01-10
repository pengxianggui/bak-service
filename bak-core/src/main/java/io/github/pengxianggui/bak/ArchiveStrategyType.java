package io.github.pengxianggui.bak;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;

import java.util.Date;

public enum ArchiveStrategyType {
    d, // 按照天数
    r; // 按照条数

    /**
     * 获取策略值
     *
     * @param strategyValue 策略值。 如：365 或 200000，按照当前策略解析出参与比较的值
     * @return 策略值
     */
    public String getStrategyValue(int strategyValue) {
        switch (this) {
            case d:
                return DateUtil.format(DateUtil.offsetDay(new Date(), -strategyValue), DatePattern.NORM_DATETIME_PATTERN);
            case r:
                return String.valueOf(strategyValue);
            default:
                throw BakException.archiveEx("策略值异常: %s", this.name());
        }
    }
}
