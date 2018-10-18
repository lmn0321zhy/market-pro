package com.lmn.common.tag;


import com.lmn.common.service.OptionsService;
import com.lmn.common.tag.BaseTag;
import com.lmn.common.utils.SpringContextHolder;
import com.lmn.common.utils.StringUtils;
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
