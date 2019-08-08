package com.lmn.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * config 配置文件
 */
@ConfigurationProperties(prefix = AppProperties.APP_PREFIX)
public class AppProperties {
    public static final String APP_PREFIX = "app";
    /**
     * 应用名称
     */
    private String name;
    /**
     * 应用描述
     */
    private String description;
    /**
     * 应用版本
     */
    private String version;
    /**
     * 应用唯一id
     */
    private String id;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
