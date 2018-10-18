package com.lmn.common.tag;


import com.lmn.common.entity.Meta;
import com.lmn.common.utils.MetaUtils;
import com.lmn.common.utils.StringUtils;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 媒体tag
 * meta_list_tag id=? type=image
 * type image 图片类型 gif,jpg,jpeg,png,bmp
 * flash swf,flv
 * media swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb
 * file doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2 已经上面所有类型
 * 不写为所有类型
 */
@Service
public class MetaListTag extends BaseTag {

    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        String id = StringUtils.toString(params.get("id"));
        String type = StringUtils.toString(params.get("type"));
        MetaUtils.findByMeta(id);
        List<Meta> list = MetaUtils.findByMeta(id, type);
        env.setVariable("metas", beansWrapper.wrap(list));
        body.render(env.getOut());
    }
}
