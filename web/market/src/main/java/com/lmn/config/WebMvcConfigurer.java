package com.lmn.config;

import cn.wenwuyun.common.config.Const;
import cn.wenwuyun.config.freemarker.FreemarkerInterceptor;
import cn.wenwuyun.modules.sys.interceptor.LogInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * MVC配置
 */
@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /**
         * 日志拦截器
         */
        registry.addInterceptor(new LogInterceptor())
                .addPathPatterns(
                        Const.getApiPath() + "/**/save*",
                        Const.getApiPath() + "/**/delete*",
                        Const.getApiPath() + "/**/update*");
        super.addInterceptors(registry);

        /**
         * 模板拦截器
         */
        registry.addInterceptor(new FreemarkerInterceptor()).addPathPatterns(Const.getFrontPath() + "/**");
        super.addInterceptors(registry);
    }
}
