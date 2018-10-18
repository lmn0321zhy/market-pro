package com.lmn.common.web;



import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lmn.common.base.ApiData;
import com.lmn.common.base.BaseController;
import com.lmn.common.config.Const;
import com.lmn.common.entity.Office;
import com.lmn.common.entity.Role;
import com.lmn.common.entity.User;
import com.lmn.common.service.OfficeService;
import com.lmn.common.service.SystemService;
import com.lmn.common.utils.UserUtils;
import com.lmn.common.utils.Collections3;
import com.lmn.common.utils.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 角色Controller
 */
@RestController
@RequestMapping(value = "${apiPath}/sys/role")
public class RoleController extends BaseController {

    @Autowired
    private SystemService systemService;

    @Autowired
    private OfficeService officeService;

    @ModelAttribute("role")
    public Role get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return systemService.getRole(id);
        } else {
            return new Role();
        }
    }

    @RequiresPermissions("sys:role:view")
    @RequestMapping(value = {"list", ""})
    public ApiData list(Role role) {
        List<Role> allRole=systemService.findAllRole();
        List<Role> roles=StringUtils.isNotBlank(role.getName())?new ArrayList<Role>():allRole;
        if(StringUtils.isNotBlank(role.getName())) {
            for (Role r : allRole) {
                if (StringUtils.isNotBlank(r.getName()) && r.getName().contains(role.getName())){
                    roles.add(r);
                }
            }
        }
        return new ApiData<>(roles);
    }

    @RequiresPermissions("sys:role:view")
    @RequestMapping(value = {"allRole", ""})
    public ApiData allRole(Role role) {
        ApiData apiData = new ApiData();
        List<Role> roles = systemService.findAllRole();
        Map<String, String> mapping = Maps.newHashMap();
        for (Role r : roles) {
            mapping.put(r.getId(), r.getName());
        }
        apiData.setData(mapping);
        return apiData;
    }

    @RequiresPermissions("sys:role:view")
    @RequestMapping(value = "form")
    public ApiData form(Role role) {
        if (role.getOffice() == null) {
            role.setOffice(UserUtils.getUser().getOffice());
        }
        /*role.setUserMenuList(systemService.findAllMenu());
        role.setUserOfficeList(officeService.findAll());*/
        /*model.addAttribute("role", role);
        model.addAttribute("menuList", systemService.findAllMenu());
        model.addAttribute("officeList", officeService.findAll());
        return new ApiData<>(model);*/
        return new ApiData<>(role);
    }

    @RequiresPermissions("sys:role:edit")
    @RequestMapping(value = "save")
    public ApiData save(@RequestBody Role role) {
        ApiData apiData = new ApiData();
        // !UserUtils.getUser().isAdmin()
        if (!UserUtils.getUser().isRootRole() && role.getSysData().equals(Const.YES)) {

            apiData.setMessage("越权操作，只有超级管理员才能修改此数据！");
            return apiData;
        }

        if (!beanValidator(apiData, role)) {
            return apiData;
        }
        if (!"true".equals(checkName(role.getOldName(), role.getName()))) {
            apiData.setMessage("保存角色'" + role.getName() + "'失败, 角色名已存在");
            return apiData;
        }
        if (!"true".equals(checkEnname(role.getOldEnname(), role.getEnname()))) {
            apiData.setMessage("保存角色'" + role.getName() + "'失败, 英文名已存在");
            return apiData;
        }
        systemService.saveRole(role);
        apiData.setMessage("保存角色'" + role.getName() + "'成功");
        return apiData;
    }

    @RequiresPermissions("sys:role:edit")
    @RequestMapping(value = "delete")
    public ApiData delete(Role role) {
        ApiData apiData = new ApiData();
        if (!UserUtils.getUser().isRootRole() && role.getSysData().equals(Const.YES)) {
            apiData.setMessage("越权操作，只有超级管理员才能修改此数据！");
            return apiData;
        }

//		if (Role.isAdmin(id)){
//			addMessage(redirectAttributes, "删除角色失败, 不允许内置角色或编号空");
////		}else if (UserUtils.getUser().getRoleIdList().contains(id)){
////			addMessage(redirectAttributes, "删除角色失败, 不能删除当前用户所在角色");
//		}else{
        systemService.deleteRole(role);
        apiData.setMessage("删除角色成功");
//		}
        return apiData;
    }

    /**
     * 角色分配页面
     *
     * @param role
     * @return
     */
    @RequiresPermissions("sys:role:edit")
    @RequestMapping(value = "assign")
    public ApiData assign(Role role) {
        User queryUser=new User(new Role(role.getId()));
        queryUser.setCurrentUser(UserUtils.getUser());
        return new ApiData<>(systemService.findUser(queryUser));
    }

    /**
     * 角色分配 -- 打开角色分配对话框
     *
     * @param role
     * @param model
     * @return
     */
    @RequiresPermissions("sys:role:view")
    @RequestMapping(value = "usertorole")
    public ApiData selectUserToRole(Role role, Model model) {
        List<User> userList = systemService.findUser(new User(new Role(role.getId())));
        model.addAttribute("role", role);
        model.addAttribute("userList", userList);
        model.addAttribute("selectIds", Collections3.extractToString(userList, "name", ","));
        model.addAttribute("officeList", officeService.findAll());
        return new ApiData<>(model);
    }

    /**
     * 角色分配 -- 根据部门编号获取用户列表
     *
     * @param officeId
     * @param request
     * @return
     */
    @RequiresPermissions("sys:role:view")
    @ResponseBody
    @RequestMapping(value = "users")
    public ApiData users(String officeId, HttpServletRequest request) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        User user = new User();
        user.setOffice(new Office(officeId));
        user.setCurrentUser(UserUtils.getUser());
        PageInfo<User> page = systemService.findUser(request, user);
        for (User e : page.getList()) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("id", e.getId());
            map.put("pId", 0);
            map.put("name", e.getName());
            mapList.add(map);
        }
        return new ApiData<>(mapList);
    }

    /**
     * 角色分配 -- 从角色中移除用户
     *
     * @param userId
     * @param roleId
     * @return
     */
    @RequiresPermissions("sys:role:edit")
    @RequestMapping(value = "outrole")
    public ApiData outrole(String userId, String roleId) {
        ApiData apiData = new ApiData();
        Role role = systemService.getRole(roleId);
        User user = systemService.getUser(userId);
        if (UserUtils.getUser().getId().equals(userId)) {
            apiData.setMessage("无法从角色【" + role.getName() + "】中移除用户【" + user.getName() + "】自己！");
        } else {
            if (user.getRoleList().size() <= 1) {
                apiData.setMessage("用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除失败！这已经是该用户的唯一角色，不能移除。");
            } else {
                Boolean flag = systemService.outUserInRole(role, user);
                if (!flag) {
                    apiData.setMessage("用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除失败！");
                } else {
                    apiData.setMessage("用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除成功！");
                }
            }
        }
        return apiData;
    }

    /**
     * 角色分配
     *
     * @param role
     * @param idsArr
     * @return
     */
    @RequiresPermissions("sys:role:edit")
    @RequestMapping(value = "assignrole")
    public ApiData assignRole(Role role, String[] idsArr) {
        StringBuilder msg = new StringBuilder();
        int newNum = 0;
        for (int i = 0; i < idsArr.length; i++) {
            User user = systemService.assignUserToRole(role, systemService.getUser(idsArr[i]));
            if (null != user) {
                msg.append("新增用户【" + user.getName() + "】到角色【" + role.getName() + "】！");
                newNum++;
            }
        }
        return new ApiData().setMessage("已成功分配 " + newNum + " 个用户" + msg);
    }

    /**
     * 验证角色名是否有效
     *
     * @param oldName
     * @param name
     * @return
     */
    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "checkName")
    public String checkName(String oldName, String name) {
        if (name != null && name.equals(oldName)) {
            return "true";
        } else if (name != null && systemService.getRoleByName(name) == null) {
            return "true";
        }
        return "false";
    }

    /**
     * 验证角色英文名是否有效
     *
     * @param oldEnname
     * @param enname
     * @return
     */
    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "checkEnname")
    public String checkEnname(String oldEnname, String enname) {
        if (enname != null && enname.equals(oldEnname)) {
            return "true";
        } else if (enname != null && systemService.getRoleByEnname(enname) == null) {
            return "true";
        }
        return "false";
    }

    /**
     * 获取当前用户所拥有的角色
     *
     * @return
     */
    @RequiresPermissions("user")
    @RequestMapping(value = "treeData")
    public ApiData treeData() {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<Role> list = systemService.findAllRole();
        Map<String, Object> map = Maps.newHashMap();
        for (int i = 0; i < list.size(); i++) {
            Role e = list.get(i);
            /*Map<String, Object> map = Maps.newHashMap();
            map.put("id", e.getId());
            map.put("name", e.getName());
            mapList.add(map);*/
            map.put(e.getName(), e.getId());
            map.put("nocheck",true);
        }
        /*return new ApiData<>(mapList);*/
        return new ApiData<>(map);
    }

    @RequestMapping(value = "userroles")
    public ApiData userRoles(@RequestParam(required = false) String id) {
        return new ApiData<>(systemService.getRoleListByUserId(id));
    }

    @RequestMapping(value = "userroleids")
    public ApiData userRoleIds(@RequestParam(required = false) String id) {
        List<String> roleIdList = Lists.newArrayList();
        for (Role r : systemService.getRoleListByUserId(id)) {
            roleIdList.add(r.getId());
        }
        return new ApiData<>(roleIdList);
    }

    @RequiresPermissions("sys:role:edit")
    @RequestMapping(value = "outrolelist")
    public ApiData outroleList(String[] userIds, String roleId) {//批量删除角色
        ApiData apiData=new ApiData();
        StringBuilder msg = new StringBuilder();
        int successFlag=0;
        List<String> failId=Lists.newArrayList();
        for(String userId:userIds){
            Role role = systemService.getRole(roleId);
            User user = systemService.getUser(userId);
            if (UserUtils.getUser().getId().equals(userId)) {
                failId.add(userId);
                msg.append("无法从角色【" + role.getName() + "】中移除用户【" + user.getName() + "】自己！");
            } else {
                if (user.getRoleList().size() <= 1) {
                    failId.add(userId);
                    msg.append("用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除失败！这已经是该用户的唯一角色，不能移除。");
                } else {
                    Boolean flag = systemService.outUserInRole(role, user);
                    if (!flag) {
                        failId.add(userId);
                        msg.append("用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除失败！");
                    }
                    else{
                        successFlag++;
                        msg.append("用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除成功！");
                    }
                }
            }
        }
        apiData.setMessage("成功移除"+successFlag+"个用户,"+msg);
        apiData.setData(failId);
        return apiData;
    }
}
