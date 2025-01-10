package io.github.pengxianggui.bak.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("task_config")
@ApiModel(value = "TaskConfig对象", description = "任务配置")
public class TaskConfig {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("数据品类id")
    private Long categoryId;

    @ApiModelProperty("数据品类名")
    private Long categoryName;

    @ApiModelProperty("任务类型(bak-备份;archive-归档)")
    private String type;

    @ApiModelProperty("执行频率。cron表达式")
    private String cron;

    @ApiModelProperty("存储路径。绝对路径")
    private String path;

    @ApiModelProperty("数据过滤条件(where)")
    private String cond;

    @ApiModelProperty("产生的文件是否zip压缩")
    private Boolean zip;

    @ApiModelProperty("是否启用(默认启用)")
    private Boolean enable;

    @ApiModelProperty("用于归档类型。阈值策略(d-启用天数阈值;r-启用条数阈值；默认d)")
    private String strategy;

    @ApiModelProperty("用于归档类型。天数或条数, (如:365 或 200000)")
    private Integer strategyValue;

    @ApiModelProperty("用于归档类型。处理类型, 超出阈值时如何处理(查看HandleType枚举类)")
    private String handleType;

    @ApiModelProperty("生成文件的保存天数")
    private Integer keepFate;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
