package com.lmn.common.web;


import com.lmn.common.base.ApiData;
import com.lmn.common.base.BaseController;
import com.lmn.common.utils.UserUtils;
import com.lmn.common.utils.CacheUtils;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 缓存Controller
 */
@RestController
@RequestMapping(value = "${apiPath}/sys/cache")
public class CacheController extends BaseController {


    @RequiresUser
    @RequestMapping(value = "deleteall")
    public ApiData deleteAll() {
        ApiData apiData = new ApiData();
        apiData.setMessage("删除所有缓存成功");
//        CacheUtils.removeAll("defaultCache");
        CacheUtils.removeAll("monitorCache");
        CacheUtils.removeAll("sysCache");
        CacheUtils.removeAll("cmsCache");
        UserUtils.clearCache();
        return apiData;
    }

    @RequiresUser
    @RequestMapping(value = "delete/{key}")
    public ApiData delete(@PathVariable String key) {
        ApiData apiData = new ApiData();
        apiData.setMessage(key + "缓存删除成功");
        CacheUtils.remove(key);
        return apiData;
    }
}
