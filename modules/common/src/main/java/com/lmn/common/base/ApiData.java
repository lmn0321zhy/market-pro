package com.lmn.common.base;


import com.lmn.common.config.Const;
import lombok.Data;

/**
 * 统一的api数据格式体
 */
@Data
public class ApiData<T> {
    /**
     * 版本号
     */
    private static String version = Const.getVersion();
    /**
     * 状态码
     */
    private int code;
    /**
     * 跳转链接
     */
    private String redirect;

    /**
     * 消息
     */
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
    private T data;

    public static String success = "success";
    public static String info = "info";
    public static String warning = "warning";
    public static String error = "error";

}
