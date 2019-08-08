package com.lmn.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * config 配置文件
 */
@ConfigurationProperties(prefix = ConfigProperties.CONFIG_PREFIX)
public class ConfigProperties {
    public static final String CONFIG_PREFIX = "config";
    /**
     * api路径
     */
    private String apiPath;
    /**
     * 前端路径
     */
    private String frontPath;
    /**
     * 前端显示后缀
     */
    private String urlSuffix;


    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    public String getFrontPath() {
        return frontPath;
    }

    public void setFrontPath(String frontPath) {
        this.frontPath = frontPath;
    }

    public String getUrlSuffix() {
        return urlSuffix;
    }

    public void setUrlSuffix(String urlSuffix) {
        this.urlSuffix = urlSuffix;
    }
}
