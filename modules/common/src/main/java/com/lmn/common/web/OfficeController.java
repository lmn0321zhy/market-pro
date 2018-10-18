package com.lmn.common.web;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lmn.common.base.ApiData;
import com.lmn.common.base.BaseController;
import com.lmn.common.config.Const;
import com.lmn.common.entity.Office;
import com.lmn.common.entity.User;
import com.lmn.common.service.OfficeService;
import com.lmn.common.utils.UserUtils;
import com.lmn.common.ui.SimpleTree;
import com.lmn.common.utils.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 机构Controller
 */
@RestController
@RequestMapping(value = "${apiPath}/sys/office")
public class OfficeController extends BaseController {

    @Autowired
    private OfficeService officeService;

    @ModelAttribute("office")
    public Office get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return officeService.get(id);
        } else {
            return new Office();
        }
    }

    @RequiresPermissions("sys:office:view")
    @RequestMapping(value = {"list", ""})
    public ApiData list(Office office, HttpServletRequest request) {
        ApiData apiData = new ApiData();
        List<Office> allOffice = officeService.findAll();
        List<Office> offices = Lists.newArrayList();
        if (office != null && office.getParent() != null && StringUtils.isNotBlank(office.getParent().getId())) {
            for (Office office1 : allOffice) {
                if (office1.getParent() != null && StringUtils.isNotBlank(office1.getParent().getId()) && office1.getParent().getId().equals(office.getParent().getId())) {
                    offices.add(office1);
                }
            }
            apiData.setData(offices);
        } else {
            apiData.setData(allOffice);
        }
        return apiData;
    }

    //@RequiresPermissions("sys:office:view")
    @RequestMapping(value = "form")
    public ApiData form(Office office) {
        User user = UserUtils.getUser();
        if (office.getParent() == null || office.getParent().getId() == null) {
            office.setParent(user.getOffice());
        }
        office.setParent(officeService.get(office.getParent().getId()));
//        if (office.getArea() == null) {
//            office.setArea(user.getOffice().getArea());
//        }
        if (StringUtils.isNotBlank(office.getId())) {
            office.setChildDeptList((List<String>) (officeService.getChildDept(office.getId()).get("childDeptList")));
        }

        // 自动获取排序号
        if (StringUtils.isBlank(office.getId()) && office.getParent() != null) {
            int size = 0;
            List<Office> list = officeService.findAll();
            for (int i = 0; i < list.size(); i++) {
                Office e = list.get(i);
                if (e.getParent() != null && e.getParent().getId() != null
                        && e.getParent().getId().equals(office.getParent().getId())) {
                    size++;
                }
            }
            office.setCode(office.getParent().getCode() + StringUtils.leftPad(String.valueOf(size > 0 ? size + 1 : 1), 3, "0"));
        }
        return new ApiData<>(office);
    }

    @RequiresPermissions("sys:office:edit")
    @RequestMapping(value = "save")
    public ApiData save(@RequestBody Office office) {
        ApiData apiData = new ApiData();

        if (!beanValidator(apiData, office)) {
            return apiData;
        }
        officeService.save(office);
        String childDeptStr = (String) (officeService.getChildDept(office.getId()).get("childDeptStr"));
        if (office.getChildDeptList() != null) {
            Office childOffice = null;
            for (String id : office.getChildDeptList()) {
                if (StringUtils.isBlank(childDeptStr) || StringUtils.isNotBlank(childDeptStr) ) {
                    childOffice = new Office();
//                    childOffice.setName(DictUtils.getDictLabel(id, "sys_office_common", "未知"));
//                    childOffice.setParent(office);
//                    childOffice.setArea(office.getArea());
                    childOffice.setType("2");
                    childOffice.setGrade(String.valueOf(Integer.valueOf(office.getGrade()) + 1));
                    childOffice.setUseable(Const.YES);
                    officeService.save(childOffice);
                }
            }
        }
        apiData.setMessage("保存机构'" + office.getName() + "'成功");
        return apiData;
    }

    @RequiresPermissions("sys:office:edit")
    @RequestMapping(value = "delete")
    public ApiData delete(Office office) {
        ApiData apiData = new ApiData();
        if (office != null && StringUtils.isNotBlank(office.getId()) && Office.isRoot(office.getId()) || office.getParent() == null || office != null && StringUtils.isBlank(office.getId())) {
            if (StringUtils.isNotBlank(office.getParentIds()) && StringUtils.isNotBlank(office.getName())
                    && (office.getName().equals("综合部") || office.getName().equals("开发部") || office.getName().equals("人力部"))) {
                officeService.delete(office);
                apiData.setMessage("删除下级机构成功");
            } else {
                apiData.setMessage("删除机构失败, 不允许删除顶级机构或编号空!");
            }
        } else {
            officeService.delete(office);
            apiData.setMessage("删除机构成功");
        }
        return apiData;
    }

    /**
     * 获取机构JSON数据。
     *
     * @param extId    排除的ID
     * @param type     类型（1：公司；2：部门/小组/其它：3：用户）
     * @param grade    显示级别
     * @param response
     * @return
     */
    @RequiresPermissions("user")
    @RequestMapping(value = "treeData")
    public ApiData treeData(@RequestParam(required = false) String extId, @RequestParam(required = false) String type, @RequestParam(required = false) String parentid,
                            @RequestParam(required = false) Long grade, @RequestParam(required = false) Boolean isAll, HttpServletResponse response) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<Office> list = officeService.findList(isAll);
        for (int i = 0; i < list.size(); i++) {
            Office e = list.get(i);
            if ((StringUtils.isBlank(extId) || (extId != null && !extId.equals(e.getId()) && e.getParentIds().indexOf("," + extId + ",") == -1))
                    && (type == null || (type != null && (type.equals("1") ? type.equals(e.getType()) : true)))
                    && (grade == null || (grade != null && Integer.parseInt(e.getGrade()) <= grade.intValue()))
                    && (parentid == null || (parentid != null && parentid.equals(e.getParent().getId())))
                    && Const.YES.equals(e.getUseable())) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", e.getId());
                map.put("pId", e.getParentId());
                map.put("pIds", e.getParentIds());
                map.put("name", e.getName());
                if (type != null && "3".equals(type)) {
                    map.put("isParent", true);
                }
                mapList.add(map);
            }
        }
        return new ApiData<>(mapList);
    }

   // @RequiresPermissions("user")
    @RequestMapping(value = "tree")
    public ApiData tree(@RequestParam(required = false) String extId,
                        @RequestParam(required = false) String id,
                        @RequestParam(required = false) Boolean self,//是否包括自身
                        HttpServletResponse response) {
        List<SimpleTree> mapList = Lists.newArrayList();
        List<Office> list = officeService.findAllList();
        for (int i = 0; i < list.size(); i++) {
            Office e = list.get(i);
            if (StringUtils.isBlank(extId) || !StringUtils.contains(e.getParentIds(), "," + extId + ",")) {
                SimpleTree tree = new SimpleTree();
                tree.setId(e.getId());
                tree.setpId(e.getParentId());
                tree.setName(e.getName());
                if (!"2".equals(e.getType())) {
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
