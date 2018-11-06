package com.lmn.common.shiro;


import org.apache.shiro.cas.CasFilter;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(ShiroProperties.class)
public class ShiroConfiguration {
    @Autowired
    private ShiroProperties properties;


    @Bean(name = "shiroFilter")
    @ConditionalOnMissingBean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean() {

        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager());
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

    /**
     * 自定义sessionManager
     * @return
     */
    @Bean(name = "sessionManager")
    public SessionManager sessionManager(){
        ShiroSessionManager shiroSessionManager = new ShiroSessionManager();
        //这里可以不设置。Shiro有默认的session管理。如果缓存为Redis则需改用Redis的管理
        shiroSessionManager.setSessionDAO(new EnterpriseCacheSessionDAO());
        return shiroSessionManager;
    }
    /**
     * 配置管理层。即安全控制层
     * @return
     */
    @Bean(name="securityManager")
    public DefaultWebSecurityManager securityManager(){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(systemAuthorizingRealm());
        //自定义session管理
        securityManager.setSessionManager(sessionManager());
        //自定义缓存实现
//        securityManager.setCacheManager(ehCacheManager());
        return  securityManager;
    }
    @Bean(name = "systemAuthorizingRealm")
    public Realm systemAuthorizingRealm() {
        SystemAuthorizingRealm systemAuthorizingRealm = new SystemAuthorizingRealm();
        return systemAuthorizingRealm;
    }
}
