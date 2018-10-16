package com.lmn.common.sys.tag;

import cn.wenwuyun.common.tag.BaseTag;
import cn.wenwuyun.modules.sys.entity.User;
import cn.wenwuyun.modules.sys.utils.UserUtils;
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
public class SysUserTag extends BaseTag {

    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        String id = params.get("id").toString();
        User user = UserUtils.getUser(id);
        env.setVariable("user", beansWrapper.wrap(user));
        body.render(env.getOut());
    }

}
