package com.lmn.common.tag;

import com.lmn.common.utils.StringUtils;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;


/**
 * tag插件基类
 */
public abstract class BaseTag extends ApplicationObjectSupport implements
        TemplateDirectiveModel, Plugin {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    protected final static BeansWrapper beansWrapper = new BeansWrapperBuilder(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS).build();

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @PostConstruct
    public void init() throws TemplateModelException {
        String className = this.getClass().getName()
                .substring(this.getClass().getName().lastIndexOf(".") + 1);
        String beanName = StringUtils.uncapitalize(className);
        String tagName = StringUtils.toUnderScoreCase(beanName);
        logger.info(tagName);
        freeMarkerConfigurer.getConfiguration().setSharedVariable(tagName,
                this.getApplicationContext().getBean(beanName));
    }

}
