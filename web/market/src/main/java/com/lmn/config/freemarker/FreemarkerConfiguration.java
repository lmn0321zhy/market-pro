package com.lmn.config.freemarker;

import cn.wenwuyun.common.security.shiro.tags.ShiroTags;
import cn.wenwuyun.common.utils.StringUtils;
import cn.wenwuyun.config.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class FreemarkerConfiguration extends FreeMarkerAutoConfiguration.FreeMarkerWebConfiguration {
    @Autowired
    private AppProperties appProperties;

    @Override
    public FreeMarkerConfigurer freeMarkerConfigurer() {
        FreeMarkerConfigurer configurer = super.freeMarkerConfigurer();

        Map<String, Object> app = new HashMap<>();
        app.put("appTitle", appProperties.getName());
        app.put("appDescription", appProperties.getDescription());
        app.put("appVersion", appProperties.getVersion());
        app.put("appId", appProperties.getId());
        app.put("shiro", new ShiroTags());
        app.put("str", new StringUtils());
        configurer.setFreemarkerVariables(app);
        return configurer;
    }
}