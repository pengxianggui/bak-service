package io.github.pengxianggui.bak.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("opr_log")
@ApiModel(value = "OprLog对象", description = "操作记录")
public class OprLog {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("操作人")
    private String operator;

    @ApiModelProperty("操作的数据类目编码")
    private String categoryCode;

    @ApiModelProperty("操作的数据类目名")
    private String categoryName;

    @ApiModelProperty("数据库名")
    private String dbName;

    @ApiModelProperty("表名")
    private String tableName;

    @ApiModelProperty("类型(bak-备份;archive-归档;restore-还原;export-导出)")
    private String type;

    @ApiModelProperty("操作是否成功")
    private Boolean success;

    @ApiModelProperty("生成的文件路径")
    private String filePath;

    @ApiModelProperty("(文件)失效日期")
    private LocalDate expiredDate;

    @ApiModelProperty("(文件)是否失效")
    private Boolean expired;

    @ApiModelProperty("where条件")
    private String cond;

    @ApiModelProperty("消息")
    private String msg;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
