package io.github.pengxianggui.bak.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("通用返回")
@Data
public class Result<T> {

    @ApiModelProperty("返回码，0：成功，其它：错误")
    private int code;

    @ApiModelProperty("返回数据")
    private T data;

    @ApiModelProperty("错误信息")
    private String msg;

    @ApiModelProperty("错误详情")
    private Object detail;

    public Result(int code, T data, String msg, Object detail) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.detail = detail;
    }

    public static <T> Result<T> success() {
        return new Result<T>(0,null,null,null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<T>(0,data,null,null);
    }

    public static <T> Result<T> success(T data, String msg) {
        return new Result<T>(0,data,msg,null);
    }

    public static <T> Result<T> fail(int code, String msg) {
        return new Result<T>(code,null,msg,null);
    }

    public static <T> Result<T> fail(int code, String msg,Object detail) {
        return new Result<T>(code,null,msg,detail);
    }

}