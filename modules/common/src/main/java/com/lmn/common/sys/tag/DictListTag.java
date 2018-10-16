package com.lmn.common.sys.tag;

import cn.wenwuyun.common.tag.BaseTag;
import cn.wenwuyun.modules.sys.entity.Dict;
import cn.wenwuyun.modules.sys.utils.DictUtils;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 字典列表tag
 */
@Service
public class DictListTag extends BaseTag {

    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        String type = params.get("type").toString();
        List<Dict> list = DictUtils.getDictList(type);

        env.setVariable("dicts", beansWrapper.wrap(list));
        body.render(env.getOut());
    }

}
