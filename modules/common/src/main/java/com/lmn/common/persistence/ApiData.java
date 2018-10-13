package com.lmn.common.persistence;

import com.lmn.common.config.Const;
import com.lmn.common.mapper.JsonMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;

import java.util.Map;

/**
 * 统一的api数据格式体
 */
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

    public ApiData() {
    }

    public ApiData(T data) {
        this.data = data;
    }

    public String getVersion() {
        return version;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getMessage() {
        return message;
    }

    public ApiData<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public ApiData<T> setData(T data) {
        if (data instanceof Model) {
            Model model = (Model) data;
            //把通知信息更新到message中
            Map temp = model.asMap();
            if (temp.get("message") != null) {
                this.setMessage(model.asMap().get("message").toString());
                temp.remove("message");
            }
        }
        this.data = data;
        return this;
    }

    public String getType() {
        if (StringUtils.isBlank(type)) {
            if (StringUtils.containsAny(message, "错误", "问题")) {
                type = "error";
            } else if (StringUtils.containsAny(message, "警告", "小心")) {
                type = "warning";
            } else if (StringUtils.containsAny(message, "注意", "提示")) {
                type = "info";
            } else if (StringUtils.containsAny(message, "成功", "恭喜")) {
                type = "success";
            }
        }
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getForceClearCache() {
        return forceClearCache;
    }

    public void setForceClearCache(Boolean forceClearCache) {
        this.forceClearCache = forceClearCache;
    }

    public String getAuthenticate() {
        return authenticate;
    }

    public void setAuthenticate(String authenticate) {
        this.authenticate = authenticate;
    }

    @Override
    public String toString() {
        return JsonMapper.toJsonString(this);
    }

    //是否存在提示信息，如果存在视为true否则为false
    @JsonIgnore
    public boolean isValid() {
        return StringUtils.isNotBlank(message);
    }

    public static String success = "success";
    public static String info = "info";
    public static String warning = "warning";
    public static String error = "error";
}
