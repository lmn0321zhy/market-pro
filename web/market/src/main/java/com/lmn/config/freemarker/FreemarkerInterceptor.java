package com.lmn.config.freemarker;

import cn.wenwuyun.common.config.Const;
import cn.wenwuyun.common.utils.FileUtils;
import cn.wenwuyun.common.utils.HttpUtils;
import cn.wenwuyun.common.utils.StringUtils;
import cn.wenwuyun.modules.cms.entity.Site;
import cn.wenwuyun.modules.cms.utils.CmsUtils;
import cn.wenwuyun.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局模板拦截器自动附加
 */
@Component
public class FreemarkerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        if (null == modelAndView) {
            return;
        }

        Site site;
        if (modelAndView.getModel().containsKey("site")) {
            site = (Site) modelAndView.getModel().get("site");
        } else {
            site = CmsUtils.getSiteByDomain(request.getServerName());
            modelAndView.addObject("site", site);
        }

        String name = modelAndView.getViewName();

        if (!StringUtils.startsWithAny(name, "redirect:", "error")) {
            // 系统配置参数
            String ctx = HttpUtils.getContextPath(request);
            modelAndView.addObject("request", request);
            modelAndView.addObject("user", UserUtils.getUser());
            modelAndView.addObject("ctx", ctx);
            modelAndView.addObject("urlSuffix", Const.getUrlSuffix());
            modelAndView.addObject("assets", "/assets");
            //站点通用静态文件存储位置
            modelAndView.addObject("assetsCommon", "/assets/common");
            //站点通用静态文件存储位置
            modelAndView.addObject("pdfjsBuild", "/assets/pdfjs/build");
            modelAndView.addObject("pdfjsWeb", "/assets/pdfjs/web");
            //站点皮肤文件地址
            modelAndView.addObject("assetsTheme", "/assets/theme/" + site.getTheme());

            //当指定模板不存在时，使用默认模板，默认模板必须存在
            if (!FileUtils.existsViewFile(String.format("%s/%s.ftl", site.getTheme(), name))) {
                name = StringUtils.substringBeforeLast(name, "/") + "/default";
            }
            modelAndView.setViewName(String.format("modules/%s/%s", site.getTheme(), name).replaceAll("/{2,}", "/"));
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

    }

}
