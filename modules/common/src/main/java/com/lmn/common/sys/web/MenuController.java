package com.lmn.common.sys.web;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lmn.common.base.ApiData;
import com.lmn.common.base.BaseController;
import com.lmn.common.sys.entity.Menu;
import com.lmn.common.sys.service.SystemService;
import com.lmn.common.sys.utils.UserUtils;
import com.lmn.common.utils.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 菜单Controller
 */
@RestController
@RequestMapping(value = "${apiPath}/sys/menu")
public class MenuController extends BaseController {

    @Autowired
    private SystemService systemService;

    @ModelAttribute("menu")
    public Menu get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return systemService.getMenu(id);
        } else {
            return new Menu();
        }
    }

    @RequiresPermissions("sys:menu:view")
    @RequestMapping(value = {""})
    public ApiData get(Menu menu) {
        ApiData<Menu> apiData = new ApiData<>();
        apiData.setData(menu);
        return apiData;
    }


    @RequiresPermissions("sys:menu:view")
    @RequestMapping(value = {"list"})
    public ApiData list(Menu menu, HttpServletRequest request) {
        ApiData<List<Menu>> apiData = new ApiData<>();
        List<Menu> list = Lists.newArrayList();

        List<Menu> sourcelist = systemService.findAllMenu();
        Menu.sortList(list, sourcelist, Menu.getRootId(), true);

        //by duchao
        List<Menu> menus = Lists.newArrayList();
        String pid = StringUtils.isBlank(request.getParameter("parentId")) ? Menu.getRootId() : request.getParameter("parentId");
        for (Menu item : list) {
            if (item.getParent() != null && StringUtils.isNotBlank(item.getParent().getId()) && item.getParent().getId().equals(pid)) {
                menus.add(item);
            }
        }
        apiData.setData(menus);

        //apiData.setData(list);
        return apiData;
    }

    @RequiresPermissions("sys:menu:view")
    @RequestMapping(value = "form")
    public ApiData form(Menu menu) {
        if (menu.getParent() == null || menu.getParent().getId() == null) {
            menu.setParent(new Menu(Menu.getRootId()));
        }
        menu.setParent(systemService.getMenu(menu.getParent().getId()));
        // 获取排序号，最末节点排序号+30
        if (StringUtils.isBlank(menu.getId())) {
            List<Menu> list = Lists.newArrayList();
            List<Menu> sourcelist = systemService.findAllMenu();
            Menu.sortList(list, sourcelist, menu.getParentId(), false);
            if (list.size() > 0) {
                menu.setSort(list.get(list.size() - 1).getSort() + 30);
            }
        }
        return new ApiData<>(menu);
    }

    @RequiresPermissions("sys:menu:edit")
    @RequestMapping(value = "save")
    public ApiData save(@RequestBody Menu menu) {
        ApiData<Model> apiData = new ApiData<>();
        if (!(UserUtils.getUser().isRootRole() || UserUtils.getUser().isAdmin())) {
            apiData.setMessage("越权操作，只有超级管理员才能添加或修改数据！");
            return apiData;
        }
        if (!beanValidator(apiData, menu)) {
            return apiData;
        }
        if (menu.getParent() == null || menu.getParent() != null && StringUtils.isBlank(menu.getParent().getId())) {
            apiData.setMessage("必须选择一个所属父级菜单！");
            return apiData;
        }

        systemService.saveMenu(menu);
        apiData.setMessage("保存菜单'" + menu.getName() + "'成功");
        return apiData;
    }

    @RequiresPermissions("sys:menu:edit")
    @RequestMapping(value = "delete")
    public ApiData delete(Menu menu) {
        ApiData apiData = new ApiData();
        if (menu != null && StringUtils.isNotBlank(menu.getId()) && Menu.getRootId().equals(menu.getId()) || menu == null || menu != null && StringUtils.isBlank(menu.getId())) {
            apiData.setMessage("删除菜单失败, 不允许删除顶级菜单或编号空!");
        } else {
            systemService.deleteMenu(menu);
            apiData.setMessage("删除菜单" + menu.getName() + "成功");
        }
        return apiData;
    }

    /**
     * 批量修改菜单排序
     */
    @RequiresPermissions("sys:menu:edit")
    @RequestMapping(value = "updateSort")
    public ApiData updateSort(String[] ids, Integer[] sorts) {
        ApiData apiData = new ApiData();

        for (int i = 0; i < ids.length; i++) {
            Menu menu = new Menu(ids[i]);
            menu.setSort(sorts[i]);
            systemService.updateMenuSort(menu);
        }
        apiData.setMessage("保存菜单排序成功!");
        return apiData;
    }

    /**
     * @param id
     * @return
     */
    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "id/{id}")
    public ApiData findSubMenu(@PathVariable final String id) {
        ApiData<List<Map<String, Object>>> apiData = new ApiData<>();
        List<Map<String, Object>> menus = Lists.newArrayList();
        List<Menu> list = systemService.findAllMenu();
        for (int i = 0; i < list.size(); i++) {
            Menu e = list.get(i);
//            if ("1".equals(e.getIsShow()) && (e.getId().equals(id) || StringUtils.contains(e.getParentIds(), "," + id + ","))) {
            if ("1".equals(e.getIsShow()) && (StringUtils.contains(e.getParentIds(), "," + id + ","))) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", e.getId());
                map.put("pId", e.getParentId());
                map.put("name", e.getName());
                map.put("href", e.getHref());
                map.put("icon", e.getIcon());
                map.put("exclusive", true);
                if (e.getId().equals(id)) {
                    map.put("expanded", true);
                }
                menus.add(map);
            }
        }
        apiData.setData(menus);
        return apiData;
    }


    /**
     * isShowHide是否显示隐藏菜单
     *
     * @param extId      排除的id
     * @param isShowHide 是否显示隐藏菜单
     * @return
     */
    @RequiresPermissions("user")
    @RequestMapping(value = "treeData")
    public ApiData treeData(@RequestParam(required = false) String extId, @RequestParam(required = false) String isShowHide) {
        ApiData<List<Map<String, Object>>> apiData = new ApiData();
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<Menu> list = systemService.findAllMenu();
        for (int i = 0; i < list.size(); i++) {
            Menu e = list.get(i);
            if (StringUtils.isBlank(extId) || (!extId.equals(e.getId()) && e.getParentIds().indexOf("," + extId + ",") == -1)) {
                if (!"1".equals(isShowHide) && e.getIsShow().equals("0")) {
                    continue;
                }
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", e.getId());
                map.put("pId", e.getParentId());
                map.put("name", e.getName());
                map.put("href", e.getHref());
                map.put("icon", e.getIcon());
                map.put("exclusive", true);
                map.put("expanded", false);
                mapList.add(map);
            }
        }
        apiData.setData(mapList);
        return apiData;
    }
}
