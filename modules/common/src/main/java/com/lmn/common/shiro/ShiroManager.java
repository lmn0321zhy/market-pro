package com.lmn.common.shiro;


import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

import static org.apache.shiro.web.mgt.CookieRememberMeManager.DEFAULT_REMEMBER_ME_COOKIE_NAME;

/**
 * Shiro Config Manager.
 */
public class ShiroManager {

//    @Bean(name = "sessionDAO")
//    @ConditionalOnMissingBean
//    public CacheSessionDAO sessionDAO(IdGen idGen, CacheManager shiroCacheManager) {
//        CacheSessionDAO cacheSessionDAO = new CacheSessionDAO();
//        cacheSessionDAO.setSessionIdGenerator(idGen);
//        cacheSessionDAO.setCacheManager(shiroCacheManager);
//        cacheSessionDAO.setActiveSessionsCacheName("activeSessionsCache");
//        return cacheSessionDAO;
//    }

/*    @Bean(name = "sessionJedisDAO")
    @ConditionalOnMissingBean
    public JedisSessionDAO sessionJedisDAO(IdGen idGen, @Value("${spring.redis.keyPrefix}") String prefix) {
        JedisSessionDAO jedisSessionDAO = new JedisSessionDAO();
        jedisSessionDAO.setSessionIdGenerator(idGen);
        jedisSessionDAO.setSessionKeyPrefix(prefix + "_session_");
        return jedisSessionDAO;
    }*/


    /**
     * 指定本系统SESSIONID, 默认为: JSESSIONID 问题: 与SERVLET容器名冲突, 如JETTY, TOMCAT 等默认JSESSIONID,
     * 当跳出SHIRO SERVLET时如ERROR-PAGE容器会为JSESSIONID重新分配值导致登录会话丢失!
     */
    @Bean(name = "sessionIdCookie")
    @ConditionalOnMissingBean
    public SimpleCookie sessionIdCookie(@Value("${app.id}") String appId) {
        SimpleCookie cookie = new SimpleCookie();
        cookie.setName(appId + ".session.id");
        return cookie;
    }

    @Bean(name = "rememberMeManager")
    @ConditionalOnMissingBean
    public RememberMeManager rememberMeManager() {
        CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
        SimpleCookie cookie = new SimpleCookie(DEFAULT_REMEMBER_ME_COOKIE_NAME);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(Cookie.ONE_YEAR);
        rememberMeManager.setCookie(cookie);
        //生成cookie加密密钥
        /*AesCipherService aesCipherService = new AesCipherService();
        System.out.println(Base64.encodeToString(aesCipherService.generateNewKey().getEncoded()));*/

        rememberMeManager.setCipherKey(Base64.decode("5RC7uBZLkByfFfJm22q/Zw=="));
        return rememberMeManager;
    }

    /**
     * 自定义会话管理配置
     */
    @Bean(name = "sessionManager")
    @ConditionalOnMissingBean
    public SessionManager sessionManager(SessionDAO sessionDAO, SimpleCookie sessionIdCookie) {
        SessionManager sessionManager = new SessionManager();
        sessionManager.setSessionDAO(sessionDAO);

        /*sessionManager.setGlobalSessionTimeout(60000);
        sessionManager.setSessionValidationInterval(60000);*/
        //session超时为30分钟
        sessionManager.setGlobalSessionTimeout(1800000);
        sessionManager.setSessionValidationInterval(120000);
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionIdCookieEnabled(true);
        sessionManager.setSessionIdCookie(sessionIdCookie);
        return sessionManager;
    }

    /**
     * 用户授权信息Cache
     */
    @Bean(name = "shiroCacheManager")
    @ConditionalOnMissingBean
    public CacheManager shiroCacheManager(EhCacheCacheManager ehCacheCacheManager) {
        EhCacheManager cacheManager = new EhCacheManager();
        cacheManager.setCacheManager(ehCacheCacheManager.getCacheManager());
        return cacheManager;
    }

/*    @Bean(name = "shiroJedisCacheManager")
    @ConditionalOnMissingBean
    public JedisCacheManager shiroJedisCacheManager(@Value("${spring.redis.keyPrefix}") String prefix) {
        JedisCacheManager jedisCacheManager = new JedisCacheManager();
        jedisCacheManager.setCacheKeyPrefix(prefix);
        return jedisCacheManager;
    }*/


    @Bean(name = "systemAuthorizingRealm")
    @DependsOn("lifecycleBeanPostProcessor")
    @ConditionalOnClass(SystemAuthorizingRealm.class)
    public SystemAuthorizingRealm systemAuthorizingRealm() {
        return new SystemAuthorizingRealm();
    }

    /**
     * 定义Shiro安全管理配置
     */
    @Bean(name = "securityManager")
    @ConditionalOnMissingBean
    public DefaultWebSecurityManager securityManager(SystemAuthorizingRealm systemAuthorizingRealm, CacheManager shiroCacheManager,
                                                     SessionManager sessionManager) {
        DefaultWebSecurityManager sm = new DefaultWebSecurityManager();
        sm.setRealm(systemAuthorizingRealm);

        sm.setCacheManager(shiroCacheManager);
        sm.setSessionManager(sessionManager);
        sm.setRememberMeManager(rememberMeManager());
        return sm;
    }

    /**
     * 保证实现了Shiro内部lifecycle函数的bean执行
     */
    @Bean(name = "lifecycleBeanPostProcessor")
    @ConditionalOnMissingBean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * AOP式方法级权限检查
     */
    @Bean(name = "defaultAdvisorAutoProxyCreator")
    @ConditionalOnMissingBean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(DefaultSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
        aasa.setSecurityManager(securityManager);
        return aasa;
    }
}
