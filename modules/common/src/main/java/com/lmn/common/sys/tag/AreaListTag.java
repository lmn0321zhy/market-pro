package com.lmn.common.sys.tag;

import cn.wenwuyun.common.tag.BaseTag;
import cn.wenwuyun.common.utils.StringUtils;
import cn.wenwuyun.modules.sys.entity.Area;
import cn.wenwuyun.modules.sys.utils.AreaUtils;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 地区tag
 */
@Service
public class AreaListTag extends BaseTag {

    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        String type = params.get("type").toString();
        String parentId = params.get("parentId").toString();
        String param = StringUtils.toString(params.get("param"));
        List<Area> list = AreaUtils.getAreaList(type, parentId, param);
        env.setVariable("areas", beansWrapper.wrap(list));
        body.render(env.getOut());
    }
}
