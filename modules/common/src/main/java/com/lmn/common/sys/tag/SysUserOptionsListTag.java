package com.lmn.common.sys.tag;

import cn.wenwuyun.common.tag.BaseTag;
import cn.wenwuyun.common.utils.SpringContextHolder;
import cn.wenwuyun.common.utils.StringUtils;
import cn.wenwuyun.modules.sys.service.OptionsService;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * 专家资源tag
 */
@Service
public class SysUserOptionsListTag extends BaseTag {
    private static OptionsService optionsService = SpringContextHolder.getBean(OptionsService.class);
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        String title = StringUtils.toString(params.get("title"));
        env.setVariable("users", beansWrapper.wrap(optionsService.findUserAndOptions(title)));
        body.render(env.getOut());
    }

}
