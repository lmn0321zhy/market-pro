package com.lmn.common.sys.tag;

import cn.wenwuyun.common.tag.BaseTag;
import cn.wenwuyun.common.utils.StringUtils;
import cn.wenwuyun.modules.sys.utils.DictUtils;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * 字典tag
 */
@Service
public class DictTag extends BaseTag {

    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        String type = params.get("type").toString();
        String defaultValue = StringUtils.toString(params.get("default"), "无效字典");
        if (params.get("label") != null) {
            env.setVariable("value", beansWrapper.wrap(DictUtils.getDictValue(params.get("label").toString(), type, defaultValue)));
        } else if (params.get("value") != null) {
            env.setVariable("value", beansWrapper.wrap(DictUtils.getDictLabel(params.get("value").toString(), type, defaultValue)));
        }
        body.render(env.getOut());
    }

}
