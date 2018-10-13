package com.lmn.common.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RequestUtils工具类
 */
public class RequestUtils {

    /**
     * 获取requerst请求
     */
    public final static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 读取请求参数转换Map对象
     *
     * @return Map对象
     */
    public final static Map readParam() {
        return readParam(getRequest());
    }

    /**
     * 读取请求参数转换Map对象
     *
     * @param request
     * @return Map 对象
     */
    @SuppressWarnings("unchecked")
    public final static Object readParam(HttpServletRequest request, String key) {
        Map result = readParam(request);
        return result != null ? result.get(key) : null;
    }

    /**
     * 读取请求参数转换Map对象
     *
     * @param request
     * @return Map 对象
     */
    @SuppressWarnings("unchecked")
    public final static Map readParam(HttpServletRequest request) {
        Map result = new HashMap();
        if (request == null) return result;
        Map<String, String[]> map = request.getParameterMap();

        String pattern = "(\\w+)\\[(\\d+)\\]\\.(\\w+)";
        String patternArr = "(\\w+)\\[(\\d+)\\]\\[(\\w+)\\]";
        Pattern r = Pattern.compile(pattern);
        Pattern r1 = Pattern.compile(patternArr);
        Map temp = null;
        List<Map> list = new ArrayList();
        int index = 0, lastindex = 0;

        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            Matcher m = r.matcher(entry.getKey());
            if (!m.find()) {
                m = r1.matcher(entry.getKey());
            }
            if (m.find()) {
                if (result.get(m.group(1)) == null)
                    result.put(m.group(1), list);
                index = StringUtils.toInteger(m.group(2));
                temp = null;

                if (list.size() > index)
                    temp = list.get(index);
                if (temp == null) {
                    temp = new HashMap();
                    list.add(temp);
                }
                if (entry.getValue().length > 1) {
                    temp.put(m.group(3), entry.getValue());
                } else {
                    temp.put(m.group(3), entry.getValue()[0]);
                }
            } else {
                String key = entry.getKey().replaceAll("[\\W]", "");
                if (key.equals("_")) continue;
                if (entry.getValue().length > 1) {
                    result.put(key, entry.getValue());
                } else {
                    result.put(key, entry.getValue()[0]);
                }
            }
        }
        return result;
    }


    /**
     * 获取网站上下文
     */
    public final static String getContextPath() {
        return getRequest().getContextPath();
    }

    /**
     * 获取网站绝对路径
     */
    public final static String getRealPath() {
        return getRequest().getSession().getServletContext().getRealPath("/");
    }

}
