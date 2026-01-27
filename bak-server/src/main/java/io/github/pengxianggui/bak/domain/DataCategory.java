package io.github.pengxianggui.bak.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("data_category")
@ApiModel(value = "DataCategory对象", description = "数据类目")
public class DataCategory {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("品类编码")
    private String code;

    @ApiModelProperty("品类名")
    private String name;

    @ApiModelProperty("库名")
    private String dbName;

    @ApiModelProperty("表名")
    private String tableName;

    @ApiModelProperty("时间字段名")
    private String timeFieldName;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
