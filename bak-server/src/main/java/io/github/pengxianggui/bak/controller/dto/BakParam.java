package io.github.pengxianggui.bak.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 输入参数，手动执行备份/导出
 *
 * @author pengxg
 * @date 2025-01-08 14:14
 */
@Data
public class BakParam {
    @ApiModelProperty(value = "数据品类编码(给了这个值，就可以不给数据库名和表名了)")
    private String categoryCode;
    @ApiModelProperty(value = "数据库名")
    private String dbName;
    @ApiModelProperty(value = "表名")
    private String tableName;
    @ApiModelProperty(value = "条件参数", example = "id<=10")
    private String cond;
    @ApiModelProperty(value = "是否zip压缩(默认true)")
    private Boolean zip = true;
    @ApiModelProperty("文件保存天数, null则一直保存")
    private Integer keepFate;
}
