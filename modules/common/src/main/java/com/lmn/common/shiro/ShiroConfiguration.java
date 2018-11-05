package com.lmn.common.shiro;


import com.lmn.common.config.MarketDataSource;
import org.apache.shiro.cas.CasFilter;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(ShiroProperties.class)
@Import(ShiroSessionManager.class)
public class ShiroConfiguration {
    @Autowired
    private ShiroProperties properties;
    @Autowired
    private SecurityManager securityManager;


    @Bean(name = "shiroFilter")
    @ConditionalOnMissingBean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean() {

        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        shiroFilter.setLoginUrl(properties.getLoginUrl());
        shiroFilter.setSuccessUrl(properties.getSuccessUrl());
        //shiroFilter.setUnauthorizedUrl(properties.getUnauthorizedUrl());

        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("authc", new FormAuthenticationFilter());
        CasFilter casFilter = new CasFilter();
        casFilter.setFailureUrl(properties.getLoginUrl());
        filters.put("cas", casFilter);
        filters.put("user", new UserFilter());

        shiroFilter.setFilters(filters);

        shiroFilter.setFilterChainDefinitionMap(properties.getFilterChainDefinitions());
        return shiroFilter;
    }
}
