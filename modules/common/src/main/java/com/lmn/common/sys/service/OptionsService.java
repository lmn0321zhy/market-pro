package com.lmn.common.sys.service;

import cn.wenwuyun.common.mapper.JsonMapper;
import cn.wenwuyun.common.service.CrudService;
import cn.wenwuyun.common.utils.DateUtils;
import cn.wenwuyun.common.utils.JedisUtils;
import cn.wenwuyun.common.utils.StringUtils;
import cn.wenwuyun.modules.sys.dao.OptionsDao;
import cn.wenwuyun.modules.sys.entity.Options;
import cn.wenwuyun.modules.sys.utils.UserUtils;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户配置信息表Service
 *
 * @author 李顺兴
 * @version 2016-01-14
 */
@Service
public class OptionsService extends CrudService<OptionsDao, Options> {

    public Options get(String id) {
        return super.get(id);
    }

    public List<Options> findList(Options options) {
        return super.findList(options);
    }

    public List<HashMap<String, Object>> findUserAndOptions(String title) {
        if (StringUtils.isBlank(title)) {
            return null;
        }
        Options options = new Options();
        options.setTitle(title);
        List<HashMap<String, String>> list = dao.findUserAndOptions(options);
        List<HashMap<String, Object>> userAndOptions = new ArrayList<>();
        for (HashMap<String, String> map : list) {
            HashMap<String, Object> map1 = new HashMap<>();
            map1.putAll(map);
            String opconten = map.get("content");
            Map content = JsonMapper.fromJsonString(opconten, Map.class);
            map1.put("content", content);
            userAndOptions.add(map1);
        }
        return userAndOptions;
    }

    public PageInfo<Options> findPage(HttpServletRequest request, Options options) {

        return super.findPage(request, options);
    }


    public void save(Options options) {
        super.save(options);
    }

    public void delete(Options options) {
        super.delete(options);
    }

    public Options findOptionByTitle(Map map) {
        return dao.findOptionByTitle(map);
    }

    public void updateHitByUser(String type) {
        updateHitByUser(type, true);
    }

    public void updateHitByUser(String type, boolean isLogin) {
        if (!isLogin) {
            JedisUtils.set("user:9527lastClickTime", DateUtils.getDateTime(), 0);
        } else {
            Map map = Maps.newHashMap();
            map.put("userId", UserUtils.getUser().getId());
            map.put("title", "alarmCount");
            Options o = dao.findOptionByTitleAndUer(map);
            if (o == null) {
                o = new Options();
                o.setId(UserUtils.getUser().getId());
                o.setTitle("alarmCount");
                Map m = Maps.newHashMap();
                m.put(type, DateUtils.getDateTime());
                o.setContent(JsonMapper.toJsonString(m));
                o.setIsNewRecord(true);
            } else {
                Map p = JsonMapper.fromJsonString(o.getContent(), Map.class);
                p.put(type, DateUtils.getDateTime());
                o.setContent(JsonMapper.toJsonString(p));
            }
            super.save(o);
            JedisUtils.set("user:" + UserUtils.getUser().getId() + "lastClickTime", DateUtils.getDateTime(), 0);
        }
    }


    public Options getHitByUser() {
        Map map = Maps.newHashMap();
        map.put("userId", UserUtils.getUser().getId());
        map.put("title", "alarmCount");
        return dao.findOptionByTitleAndUer(map);
    }
}