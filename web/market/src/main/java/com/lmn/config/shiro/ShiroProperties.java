package com.lmn.config.shiro;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * Configuration properties for Shiro.
 */
@ConfigurationProperties(prefix = "shiro")
public class ShiroProperties {

    /**
     * URL of login
     */
    private String loginUrl;
    /**
     * URL of success
     */
    private String successUrl;
    /**
     * URL of unauthorized
     */
    private String unauthorizedUrl;
    /**
     * filter chain
     */
    private Map<String, String> filterChainDefinitions;


    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public String getUnauthorizedUrl() {
        return unauthorizedUrl;
    }

    public void setUnauthorizedUrl(String unauthorizedUrl) {
        this.unauthorizedUrl = unauthorizedUrl;
    }

    public Map<String, String> getFilterChainDefinitions() {
        return filterChainDefinitions;
    }

    public void setFilterChainDefinitions(Map<String, String> filterChainDefinitions) {
        this.filterChainDefinitions = filterChainDefinitions;
    }


}
