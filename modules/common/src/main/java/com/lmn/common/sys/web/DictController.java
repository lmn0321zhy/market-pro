package com.lmn.common.sys.web;

import cn.wenwuyun.common.persistence.ApiData;
import cn.wenwuyun.common.utils.StringUtils;
import cn.wenwuyun.common.web.BaseController;
import cn.wenwuyun.modules.sys.entity.Dict;
import cn.wenwuyun.modules.sys.service.DictService;
import cn.wenwuyun.modules.sys.utils.DictUtils;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 字典Controller
 */
@RestController
@RequestMapping(value = "${apiPath}/sys/dict")
public class DictController extends BaseController {

    @Autowired
    private DictService dictService;

    @ModelAttribute("dict")
    public Dict get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return dictService.get(id);
        } else {
            return new Dict();
        }
    }

    //    @RequiresPermissions("sys:dict:view")
    @RequestMapping(value = {"list", ""})
    public ApiData list(Dict dict, HttpServletRequest request) {
        PageInfo<Dict> page = dictService.findPage(request, dict);
        ApiData<PageInfo<Dict>> apiData = new ApiData<>();
        apiData.setData(page);
        return apiData;
    }

    //    @RequiresPermissions("sys:dict:view")
    @RequestMapping(value = "form")
    public ApiData<Dict> form(Dict dict) {
        return new ApiData<>(dict);
    }

    @RequiresPermissions("sys:dict:edit")
    @RequestMapping(value = "save")//@Valid
    public ApiData save(@RequestBody Dict dict) {
        ApiData apiData = new ApiData();
        if (!beanValidator(apiData, dict)) {
            return apiData;
        }
        dictService.save(dict);
        apiData.setMessage("保存字典'" + dict.getLabel() + "'成功");
        return apiData;
    }

    @RequiresPermissions("sys:dict:edit")
    @RequestMapping(value = "delete")
    public ApiData delete(Dict dict) {
        ApiData apiData = new ApiData();
        dictService.delete(dict);
        apiData.setMessage("删除字典成功");
        return apiData;
    }

    @RequiresPermissions("user")
    @RequestMapping(value = "treeData")
    public ApiData treeData(@RequestParam(required = false) String type, HttpServletResponse response) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        Dict dict = new Dict();
        dict.setType(type);
        List<Dict> list = dictService.findList(dict);
        for (int i = 0; i < list.size(); i++) {
            Dict e = list.get(i);
            Map<String, Object> map = Maps.newHashMap();
            map.put("id", e.getId());
            map.put("value",e.getValue());
            map.put("pId", e.getParentId());
            map.put("name", StringUtils.replace(e.getLabel(), " ", ""));
            mapList.add(map);
        }
        return new ApiData<>(mapList);
    }

    @RequestMapping(value = "listData")
    public ApiData listData(@RequestParam(required = false) String type) {
        Dict dict = new Dict();
        dict.setType(type);
        return new ApiData<>(dictService.findList(dict));
    }

    @ResponseBody
    @RequestMapping(value = {"type"})
    public List<String> typeList() {
        return dictService.findTypeList();
    }

    /**
     * 获取同类型的字典列表
     */
    @RequestMapping(value = "type/{type}")
    public ApiData getDictList(@PathVariable String type) {
        return new ApiData<>(DictUtils.getDictList(type));
    }

}
