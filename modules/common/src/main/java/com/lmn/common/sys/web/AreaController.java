package com.lmn.common.sys.web;

import cn.wenwuyun.common.persistence.ApiData;
import cn.wenwuyun.common.ui.SimpleTree;
import cn.wenwuyun.common.utils.StringUtils;
import cn.wenwuyun.common.web.BaseController;
import cn.wenwuyun.modules.sys.entity.Area;
import cn.wenwuyun.modules.sys.service.AreaService;
import cn.wenwuyun.modules.sys.utils.UserUtils;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 区域Controller
 */
@RestController
@RequestMapping(value = "${apiPath}/sys/area")
public class AreaController extends BaseController {

    @Autowired
    private AreaService areaService;

    @ModelAttribute("area")
    public Area get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return areaService.get(id);
        } else {
            return new Area();
        }
    }

    @RequiresPermissions("sys:area:view")
    @RequestMapping(value = {"list", ""})
    public ApiData list(Area area, HttpServletRequest request) {
        area.setName(request.getParameter("name"));
        area.setCurrentUser(UserUtils.getUser());
        PageInfo<Area> page = areaService.findPage(request, area);
        ApiData<PageInfo<Area>> apiData = new ApiData<>();
        apiData.setData(page);
        return apiData;
    }

    @RequestMapping(value = {"listchild"})
    public ApiData childList(Area area, HttpServletRequest request) {
        ApiData<List<Area>> apiData = new ApiData<>();
        apiData.setData(areaService.findByParentId(area));
        return apiData;
    }

//    @RequiresPermissions("sys:area:view")
    @RequestMapping(value = "form")
    public ApiData<Area> form(Area area) {
        ApiData<Area> apiData = new ApiData<>();
        if (area.getParent() == null || area.getParent().getId() == null) {
            area.setParent(UserUtils.getUser().getOffice().getArea());
        }
        //area.setParent(areaService.get(area.getParent().getId()));
        apiData.setData(area);
        return apiData;
    }

    @RequiresPermissions("sys:area:edit")
    @RequestMapping(value = "save")
    public ApiData save(@RequestBody Area area) {
        ApiData<Area> apiData = new ApiData<>();
        if (!beanValidator(apiData, area)) {
            return apiData;
        }
        areaService.save(area);
        apiData.setMessage("保存区域'" + area.getName() + "'成功");
        return apiData;
    }

    @RequiresPermissions("sys:area:edit")
    @RequestMapping(value = "delete")
    public ApiData delete(@RequestBody Area area) {
        ApiData<Area> apiData = new ApiData<>();
        areaService.delete(area);
        apiData.setMessage("删除区域成功");
        return apiData;
    }


    /**
     * @param extId 排除的父节点id
     * @param self  是否排除自身
     */
    @RequiresPermissions("user")
    @RequestMapping(value = "treeData")
    public ApiData treeData(@RequestParam(required = false) String extId,
                            @RequestParam(required = false) String id,
                            @RequestParam(required = false) Boolean self,//是否包括自身
                            HttpServletResponse response) {
        List<SimpleTree> mapList = Lists.newArrayList();

        List<Area> list = UserUtils.getAreaList();
        for (int i = 0; i < list.size(); i++) {
            Area e = list.get(i);
            if (StringUtils.isBlank(extId) || !StringUtils.contains(e.getParentIds(), "," + extId + ",")) {
                SimpleTree tree = new SimpleTree();
                tree.setId(e.getId());
                tree.setpId(e.getParentId());
                tree.setName(e.getName());
                //非地市区县表明还有下级节点
                if (!"4".equals(e.getType())) {
                    tree.setIsParent(true);
                }

                if (StringUtils.isBlank(id)) {
                    mapList.add(tree);
                } else if (StringUtils.isNotBlank(id)) {
                    if (id.equals(e.getParentId()) || BooleanUtils.isTrue(self) && e.getId().equals(id)) {
                        mapList.add(tree);
                    }
                }
            }
        }
        return new ApiData<>(mapList);
    }

}
