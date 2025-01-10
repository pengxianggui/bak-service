package io.github.pengxianggui.bak.controller.dto;

import io.github.pengxianggui.bak.ArchiveStrategyType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * 输入参数，手动归档
 *
 * @author pengxg
 * @date 2025-01-08 14:19
 */
@Data
public class ArchiveParam extends BakParam {
    @ApiModelProperty(value = "排序的时间字段名")
    private String timeFieldName;

    @ApiModelProperty(value = "策略,d-天数;r-行数。默认d", required = true, example = "d", allowableValues = "d,r")
    private ArchiveStrategyType strategy = ArchiveStrategyType.d;

    @Positive(message = "策略值必须为正整数")
    @NotNull(message = "策略值必输")
    @ApiModelProperty(value = "策略值(若策略为d, 此值为365, 则表示将365天以前的数据进行归档;若策略为r, 则表示只保留最新的365行数据，其余归档)", required = true, example = "365", allowableValues = "(0, infinity]")
    private int strategyValue;
}
