package com.lmn.common.sys.web;


import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lmn.common.base.ApiData;
import com.lmn.common.base.BaseController;
import com.lmn.common.config.Const;
import com.lmn.common.sys.entity.Role;
import com.lmn.common.sys.entity.User;
import com.lmn.common.sys.service.OptionsService;
import com.lmn.common.sys.service.SystemService;
import com.lmn.common.sys.utils.UserUtils;
import com.lmn.common.utils.BeanValidators;
import com.lmn.common.utils.DateUtils;
import com.lmn.common.utils.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;

/**
 * 用户Controller
 */
@RestController
@RequestMapping(value = "${apiPath}/sys/user")
public class UserController extends BaseController {

    @Autowired
    private SystemService systemService;
    @Autowired
    private OptionsService optionsService;

    @ModelAttribute
    public User get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return systemService.getUser(id);
        } else {
            return new User();
        }
    }

    @RequiresPermissions("sys:user:view")
    @RequestMapping(value = {"list", ""})
    public ApiData list(User user, HttpServletRequest request, HttpServletResponse response) {
        user.setCurrentUser(UserUtils.getUser());
        PageInfo<User> page = systemService.findUser(request, user);
        return new ApiData<>(page);
    }


    @RequiresPermissions("sys:user:view")
    @RequestMapping(value = "form")
    public ApiData form(User user, Model model) {
        if (user.getCompany() == null || user.getCompany().getId() == null) {
            user.setCompany(UserUtils.getUser().getCompany());
        }
        if (user.getOffice() == null || user.getOffice().getId() == null) {
            user.setOffice(UserUtils.getUser().getOffice());
        }
        Map map = Maps.newHashMap();
        map.put("user", user);
        map.put("allRoles", systemService.findAllRole());
        /*model.addAttribute("user", user);
        model.addAttribute("allRoles", systemService.findAllRole());
        return new ApiData<>(model);*/
        return new ApiData<>(map);
    }

    @RequiresPermissions("sys:user:edit")
    @RequestMapping(value = "save")
    public ApiData save(@RequestBody User user) {
        ApiData apiData = new ApiData();
        User queryUser=new User();
        queryUser.setNo(user.getNo());
        queryUser.setCurrentUser(UserUtils.getUser());
        if(StringUtils.isBlank(user.getId()) && StringUtils.isNotBlank(user.getNo()) &&  systemService.findUser(queryUser)!=null && systemService.findUser(queryUser).size()>0){
            apiData.setMessage("工号已存在，请重新输入！");
            return apiData;
        }
        if (StringUtils.isNotBlank(user.getId())) {
            User oldUser = systemService.getUser(user.getId());
            user.setPassword(oldUser.getPassword());
        }

        // 如果新密码为空，则不更换密码
        if (StringUtils.isNotBlank(user.getNewPassword())) {
            user.setPassword(SystemService.entryptPassword(user.getNewPassword()));
        }
        if (!beanValidator(apiData, user)) {
            return apiData;
        }

        if (!"true".equals(checkLoginName(user.getOldLoginName(), user.getLoginName()))) {
            apiData.setMessage("保存用户'" + user.getLoginName() + "'失败，登录名已存在");
            return apiData;
        }

        // 角色数据有效性验证，过滤不在授权内的角色
        List<Role> roleList = Lists.newArrayList();
        List<String> roleIdList = user.getRoleIdList();
        for (Role r : systemService.findAllRole()) {
            if (roleIdList.contains(r.getId())) {
                roleList.add(r);
            }
        }
        user.setRoleList(roleList);
        // 保存用户信息
        systemService.saveUser(user);
        // 清除当前用户缓存
        if (user.getLoginName().equals(UserUtils.getUser().getLoginName())) {
            UserUtils.clearCache();
            //UserUtils.getCacheMap().clear();
        }
        apiData.setMessage("保存用户'" + user.getLoginName() + "'成功");
        return apiData;
    }

    @RequiresPermissions("sys:user:edit")
    @RequestMapping(value = "delete")
    public ApiData delete(User user) {
        ApiData apiData = new ApiData();
        if (UserUtils.getUser().getId().equals(user.getId())) {
            apiData.setMessage("删除用户失败, 不允许删除当前用户");
        } else if (User.isAdmin(user.getId())) {
            apiData.setMessage("删除用户失败, 不允许删除超级管理员用户");
        } else {
            systemService.deleteUser(user);
            apiData.setMessage("删除用户成功");
        }
        return apiData;
    }

    /**
     * 导出用户数据
     *
     * @param user
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("sys:user:view")
    @RequestMapping(value = "export", method = RequestMethod.POST)
    public ApiData exportFile(User user, HttpServletRequest request, HttpServletResponse response) {
        ApiData apiData = new ApiData();
        try {
            String fileName = "用户数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            PageInfo<User> page = systemService.findUser(request, user);
//            new ExportExcel("用户数据", User.class).setDataList(page.getList()).write(response, fileName).dispose();
            return null;
        } catch (Exception e) {
            apiData.setMessage("导出用户失败！失败信息：" + e.getMessage());
        }
        return apiData;
    }

    /**
     * 导入用户数据
     *
     * @param file
     * @return
     */
    @RequiresPermissions("sys:user:edit")
    @RequestMapping(value = "import", method = RequestMethod.POST)
    public ApiData importFile(MultipartFile file) {
        ApiData apiData = new ApiData();
//        try {
//            int successNum = 0;
//            int failureNum = 0;
//            StringBuilder failureMsg = new StringBuilder();
//            ImportExcel ei = new ImportExcel(file, 1, 0);
//            List<User> list = ei.getDataList(User.class);
//            for (User user : list) {
//                try {
//                    if ("true".equals(checkLoginName("", user.getLoginName()))) {
//                        user.setPassword(SystemService.entryptPassword("123456"));
//                        BeanValidators.validateWithException(validator, user);
//                        systemService.saveUser(user);
//                        successNum++;
//                    } else {
//                        failureMsg.append("<br/>登录名 " + user.getLoginName() + " 已存在; ");
//                        failureNum++;
//                    }
//                } catch (ConstraintViolationException ex) {
//                    failureMsg.append("<br/>登录名 " + user.getLoginName() + " 导入失败：");
//                    List<String> messageList = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
//                    for (String message : messageList) {
//                        failureMsg.append(message + "; ");
//                        failureNum++;
//                    }
//                } catch (Exception ex) {
//                    failureMsg.append("<br/>登录名 " + user.getLoginName() + " 导入失败：" + ex.getMessage());
//                }
//            }
//            if (failureNum > 0) {
//                failureMsg.insert(0, "，失败 " + failureNum + " 条用户，导入信息如下：");
//            }
//            apiData.setMessage("已成功导入 " + successNum + " 条用户" + failureMsg);
//        } catch (Exception e) {
//            apiData.setMessage("导入用户失败！失败信息：" + e.getMessage());
//        }
        return apiData;
    }

    /**
     * 下载导入用户数据模板
     *
     * @param response
     * @return
     */
    @RequiresPermissions("sys:user:view")
    @RequestMapping(value = "import/template")
    public ApiData importFileTemplate(HttpServletResponse response) {
        ApiData apiData = new ApiData();
        try {
            String fileName = "用户数据导入模板.xlsx";
            List<User> list = Lists.newArrayList();
            list.add(UserUtils.getUser());
//            new ExportExcel("用户数据", User.class, 2).setDataList(list).write(response, fileName).dispose();
            return null;
        } catch (Exception e) {
            apiData.setMessage("导入模板下载失败！失败信息：" + e.getMessage());
        }
        return apiData;
    }

    /**
     * 验证登录名是否有效
     *
     * @param oldLoginName
     * @param loginName
     * @return
     */
    @RequiresPermissions("sys:user:edit")
    @RequestMapping(value = "checkLoginName")
    public String checkLoginName(String oldLoginName, String loginName) {
        if (loginName != null && loginName.equals(oldLoginName)) {
            return "true";
        } else if (loginName != null && systemService.getUserByLoginName(loginName) == null) {
            return "true";
        }
        return "false";
    }

    /**
     * 用户信息显示及保存
     *
     * @param user
     * @param model
     * @return
     */
    @RequiresPermissions("user")
    @RequestMapping(value = "info")
    public ApiData<Model> info(User user, Model model) {
        ApiData<Model> apiData = new ApiData<>();
        User currentUser = UserUtils.getUser();
        if (StringUtils.isNotBlank(user.getName())) {
            currentUser.setEmail(user.getEmail());
            currentUser.setPhone(user.getPhone());
            currentUser.setMobile(user.getMobile());
            currentUser.setRemarks(user.getRemarks());
            currentUser.setPhoto(user.getPhoto());
            systemService.updateUserInfo(currentUser);
            apiData.setMessage("保存用户信息成功");
        }
        model.addAttribute("user", currentUser);
        model.addAttribute("Const", new Const());
        apiData.setData(model);
        return apiData;
    }

    /**
     * 返回用户信息
     *
     * @return
     */
    @RequiresPermissions("user")
    @RequestMapping(value = "infoData")
    public ApiData infoData() {
        return new ApiData<>(UserUtils.getUser());
    }

    /**
     * 修改个人用户密码
     *
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @RequiresPermissions("user")
    @RequestMapping(value = "modifyPwd")
    public ApiData<User> modifyPwd(String oldPassword, String newPassword) {
        ApiData<User> apiData = new ApiData<>();
        User user = UserUtils.getUser();
        if (StringUtils.isNotBlank(oldPassword) && StringUtils.isNotBlank(newPassword)) {

            if (SystemService.validatePassword(oldPassword, user.getPassword())) {
                systemService.updatePasswordById(user.getId(), user.getLoginName(), newPassword);
                apiData.setMessage("修改密码成功");
            } else {
                apiData.setMessage("修改密码失败，旧密码错误");
            }
        } else {
            apiData.setMessage("输入无效");
        }
        apiData.setData(user);
        return apiData;
    }

    @RequiresPermissions("user")
    @RequestMapping(value = "treeData")
    public ApiData treeData(@RequestParam(required = false) String officeId, HttpServletResponse response) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<User> list = systemService.findUserByOfficeId(officeId);
        for (int i = 0; i < list.size(); i++) {
            User e = list.get(i);
            Map<String, Object> map = Maps.newHashMap();
            //map.put("id", "u_" + e.getId());
            map.put("id", e.getId());
            map.put("pId", officeId);
            map.put("name", StringUtils.replace(e.getName(), " ", ""));
            mapList.add(map);
        }
        return new ApiData<>(mapList);
    }

    @RequiresPermissions("user")
    @RequestMapping(value = "findusername")
    public List<Map> findUserName(User user){
        List<Map> mapList = Lists.newArrayList();
        List<User> list=systemService.findUserByName(user);
        for(int i=0;i<list.size();i++){
            User u=list.get(i);
            Map<String,Object> map=Maps.newHashMap();
            map.put("id",u.getId());
            map.put("name",u.getName());
            mapList.add(map);
        }
        return mapList;
    }
}
