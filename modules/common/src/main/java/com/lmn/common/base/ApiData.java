package com.lmn.common.base;


import com.lmn.common.config.Const;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 统一的api数据格式体
 */
@Data
@ApiModel(description = "返回响应数据")
public class ApiData<T> {
    /**
     * 版本号
     */
    private static String version = Const.getVersion();
    /**
     * 状态码
     */
    @ApiModelProperty(value = "状态码")
    private int code;
    /**
     * 跳转链接
     */
    private String redirect;

    /**
     * 消息
     */
    @ApiModelProperty(value = "错误信息")
    private String message;

    /**
     * 消息类型
     */
    private String type;


    /**
     * 强制清除客户端缓存
     */
    private Boolean forceClearCache;

    /**
     * 授权状态
     */
    private String authenticate;


    /**
     * 数据内容
     */
    @ApiModelProperty(value = "返回的数据")
    private T data;

    public ApiData(T data) {
        this.data = data;
    }

    public ApiData() {
    }

    public static String success = "success";
    public static String info = "info";
    public static String warning = "warning";
    public static String error = "error";

}
