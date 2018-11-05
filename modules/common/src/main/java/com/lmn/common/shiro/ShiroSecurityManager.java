package com.lmn.common.shiro;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;

/**
 * Created by lmn on 2018-11-05.
 */
public class ShiroSecurityManager {
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
    private Realm systemAuthorizingRealm() {
        SystemAuthorizingRealm systemAuthorizingRealm = new SystemAuthorizingRealm();
        return systemAuthorizingRealm;
    }
}
