package com.lmn.common.sys.web;

import cn.wenwuyun.common.persistence.ApiData;
import cn.wenwuyun.common.utils.StringUtils;
import cn.wenwuyun.common.web.BaseController;
import cn.wenwuyun.modules.sys.entity.Mdict;
import cn.wenwuyun.modules.sys.service.MdictService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

/**
 * 多级字典Controller
 *
 * @version 2013-5-15
 */
@Controller
@RequestMapping(value = "${apiPath}/sys/mdict")
public class MdictController extends BaseController {

    @Autowired
    private MdictService mdictService;

    @ModelAttribute("dict")
    public Mdict get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return mdictService.get(id);
        } else {
            return new Mdict();
        }
    }

//    @RequiresPermissions("sys:dict:view")
    @RequestMapping(value = {"mdict", ""})
    @ResponseBody
    public ApiData list(Mdict mdict) {
        ApiData apiData = new ApiData();
        apiData.setData(mdictService.findAll(mdict));
        return apiData;
    }

//    @RequiresPermissions("sys:dict:view")
    @RequestMapping(value = "form")
    @ResponseBody
    public Mdict form(Mdict mdict) {
        mdict.setParent(mdictService.get(mdict.getId()));
        return mdict;
    }

    @RequiresPermissions("sys:dict:edit")
    @RequestMapping(value = "save")
    @ResponseBody
    public ApiData save(Mdict mdict) {
        ApiData apiData = new ApiData();
        if (!beanValidator(apiData, mdict)) {
            return apiData;
        }
        mdictService.save(mdict);
        apiData.setMessage("保存多级字典'" + mdict.getName() + "'成功!!!");
        return apiData;
    }


    @RequiresPermissions("sys:dict:edit")
    @RequestMapping(value = "delete")
    @ResponseBody
    public ApiData delete(Mdict mdict, RedirectAttributes redirectAttributes) {
        ApiData apiData = new ApiData();
        mdictService.delete(mdict);
        apiData.setMessage("删除多级字典成功");
        return apiData;
    }

    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "treeData")
    public List<Map<String, Object>> treeData(@RequestParam(required = false) String extId, @RequestParam(required = false) Boolean split, @RequestParam(required = false) String stationId) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        Boolean flag = StringUtils.isNotBlank(stationId);
        Map<String, String> menuNameMap = Maps.newHashMap();
        List<Mdict> list;
        if (flag)
            list = mdictService.findAllListByStation(stationId);
        else
            list = mdictService.findAll(new Mdict());

        for (int i = 0; i < list.size(); i++) {
            Mdict e = list.get(i);
            if (StringUtils.isBlank(extId) || (extId != null && !extId.equals(e.getId()) && e.getParentIds().indexOf("," + extId + ",") == -1)) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", e.getId());
                map.put("pId", e.getParentId());
                map.put("icon", e.getIcon());
                map.put("sort", e.getSort());
                map.put("name", e.getName());
                if (e.getId().equals("1") || e.getId().equals("977afbb9d0a141dc9fd3079494503880") ||
                        e.getId().equals("af062c9df7f342c6b2db9a6823e94e99") || e.getId().equals("78091adb3b8548298fd97270b3fda1bc")
                        || e.getId().equals("dff31882c7db42d9af02f49ded1e8efc") || e.getId().equals("ee892afdc9504555b3b1e77c60f7ad36")) {
                    map.put("nocheck", true);
                }
                if (flag) {

                    for (Mdict mdict : list) {
                        if (mdict.getParentIds().contains(e.getId())) {
                            Integer num = e.getNum();
                            e.setNum(num + mdict.getNum());
                        }
                    }
                    map.put("num", e.getNum());
//                    map.put("name", e.getName() +" "+  e.getNum());
                }
                mapList.add(map);
            }
        }
        return mapList;
    }


    @ResponseBody
    @RequestMapping("child")
    public List<Mdict> findMdictByParentId(@RequestParam String parentId) {
        if (StringUtils.isBlank(parentId)) return Lists.newArrayList();
        Mdict mdict = new Mdict();
        mdict.setParentId(parentId);
        return mdictService.findByParentId(mdict);
    }
}
