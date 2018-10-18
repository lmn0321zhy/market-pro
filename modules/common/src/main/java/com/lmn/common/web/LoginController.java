package com.lmn.common.web;

import com.lmn.common.base.ApiData;
import com.lmn.common.base.BaseController;
import com.lmn.common.config.Const;
import com.lmn.common.mapper.JsonMapper;
import com.lmn.common.security.FormAuthenticationFilter;
import com.lmn.common.security.Principal;
import com.lmn.common.security.UsernamePasswordToken;
import com.lmn.common.security.shiro.session.SessionDAO;
import com.lmn.common.servlet.ValidateCodeServlet;
import com.lmn.common.entity.Office;
import com.lmn.common.entity.Options;
import com.lmn.common.entity.Role;
import com.lmn.common.entity.User;
import com.lmn.common.service.OptionsService;
import com.lmn.common.service.SystemService;
import com.lmn.common.utils.UserUtils;
import com.lmn.common.utils.IdGen;
import com.lmn.common.utils.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录Controller
 */
@RestController
@RequestMapping("${apiPath}/auth")
public class LoginController extends BaseController {

    @Autowired
    private SessionDAO sessionDAO;
    @Autowired
    private SystemService systemService;
    @Autowired
    private OptionsService optionsService;

    /**
     * 管理登录
     */
    @RequestMapping(value = "login", method = RequestMethod.GET)
    public ApiData login(HttpServletRequest request, HttpServletResponse response, Model model) {
//        Principal principal = UserUtils.getPrincipal();
        Subject subject = SecurityUtils.getSubject();
        Principal principal = (Principal) subject.getPrincipal();
        ApiData<User> apiData = new ApiData<>();
        if (logger.isDebugEnabled()) {
            logger.debug("login, active session size: {}", sessionDAO.getActiveSessions(false).size());
        }
        // 如果已经登录，则跳转到管理首页
        if (principal != null) {
            apiData.setAuthenticate("true");
            apiData.setData(UserUtils.getUser());
            //登录成功后清除验证码
            UserUtils.isValidateCodeLogin(principal.getName(), false, true);
        } else {
            apiData.setAuthenticate("false");
        }

        return apiData;
    }

    /**
     * 登录失败，真正登录的POST请求由Filter完成
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ApiData loginFail(HttpServletRequest request, HttpServletResponse response, Model model) {
        Principal principal = UserUtils.getPrincipal();
        Map data = new HashMap<>();
        ApiData apiData = new ApiData<>(data);
        // 如果已经登录，则跳转到管理首页
        if (principal != null) {
            //data.put("isValidateCodeLogin", false);
            apiData.setData(UserUtils.getUser());
            apiData.setAuthenticate("true");
            UserUtils.isValidateCodeLogin(principal.getLoginName(), false, true);
            return apiData;
        }

        String username = WebUtils.getCleanParam(request, FormAuthenticationFilter.DEFAULT_USERNAME_PARAM);
        //boolean rememberMe = WebUtils.isTrue(request, FormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM);
        String exception = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
        String message = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_MESSAGE_PARAM);

        if (StringUtils.isBlank(message) || StringUtils.equals(message, "null")) {
            message = "用户或密码错误, 请重试.";
        }
        apiData.setMessage(message);

        //model.addAttribute(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM, username);
        //model.addAttribute(FormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM, rememberMe);
        //model.addAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME, exception);

        if (logger.isDebugEnabled()) {
            logger.debug("login fail, active session size: {}, message: {}, exception: {}",
                    sessionDAO.getActiveSessions(false).size(), message, exception);
        }

        // 非授权异常，登录失败，验证码加1。
        if (!UnauthorizedException.class.getName().equals(exception)) {
            data.put("isValidateCodeLogin", UserUtils.isValidateCodeLogin(username, true, false));
        }

        // 验证失败清空验证码
        request.getSession().setAttribute(ValidateCodeServlet.VALIDATE_CODE, IdGen.uuid());

        apiData.setAuthenticate("false");
        return apiData;
    }

    /**
     * 退出登录
     */
    @RequestMapping(value = "logout")
    public ApiData logout(Model model) {
        ApiData<Model> apiData = new ApiData<>();

        Subject subject = UserUtils.getSubject();

        // 如果用户已经登录则退出登录状态
        if (subject != null) {
            subject.logout();
        }
        apiData.setAuthenticate("false");
        apiData.setData(model);
        return apiData;
    }

    /**
     * 返回用户信息
     */
    @RequiresPermissions("user")
    @RequestMapping(value = "systeminfo")
    public ApiData info(HttpServletRequest request, HttpServletResponse response, Model model) {
        ApiData<Model> apiData = new ApiData<>();

        if (logger.isDebugEnabled()) {
            logger.debug("show index, active session size: {}", sessionDAO.getActiveSessions(false).size());
        }
        apiData.setAuthenticate("true");

        User user = UserUtils.getUser();
        model.addAttribute("appName", Const.getAppName());
        model.addAttribute("userName", user.getName());
        model.addAttribute("company", user.getCompany());
        model.addAttribute("office", user.getOffice());
        List<String> roles = new ArrayList<>();
        for (Role role : UserUtils.getRoleList()) {
            roles.add(role.getEnname());
        }
        model.addAttribute("roles", roles);
        model.addAttribute("user", user);

        model.addAttribute("navMenu", UserUtils.getMenuListByLevel("1", 2));
        //model.addAttribute("navMenu", UserUtils.getMenuList("2234dc5afa234c049de58836d0c81edc"));

        apiData.setData(model);
        return apiData;
    }


    /**
     * 前台用户注册
     */
    @RequestMapping(value = "register", method = RequestMethod.POST)
    @ResponseBody
    public ApiData register(HttpServletRequest request, User user) {
        ApiData apiData = new ApiData();
        Principal principal = UserUtils.getPrincipal();
        // 如果已经登录，则跳转到前台首页
        if (principal != null) {
            apiData.setRedirect("/");
            return apiData;
        }
        // 校验登录验证码
        Session session = UserUtils.getSession();
        String code = (String) session.getAttribute(ValidateCodeServlet.VALIDATE_CODE);
        if (request.getParameter("validateCode") == null || !request.getParameter("validateCode").toUpperCase().equals(code)) {
            apiData.setMessage("验证码错误, 请重试.");
        } else {
            if (!"true".equals(checkLoginName(user.getLoginName()))) {
                apiData.setMessage("登录名已存在,请使用新的用户名");
            } else {
                //设置默认的机构为前台注册机构
                Office office = new Office("67bab3c2008e4b50807ee8710145aaaf");
                user.setCompany(office);
                user.setOffice(office);
                if (beanValidator(apiData, user)) {
                    user.setPassword(SystemService.entryptPassword(user.getPassword()));
                    systemService.saveRegiUser(user);
                    apiData.setMessage("注册用户'" + user.getLoginName() + "'成功,请登录。");
                    if (hasOptions(request)) {
                        apiData = saveoptions(request, apiData);
                        apiData.setData(user);
                    }
                    return apiData;
                }
            }
        }
        return apiData;
    }

    private boolean hasOptions(HttpServletRequest request) {

        if (StringUtils.isNotBlank(request.getParameter("sex")) || StringUtils.isNotBlank(request.getParameter("type")) || StringUtils.isNotBlank(request.getParameter("number"))
                || StringUtils.isNotBlank(request.getParameter("companyName")) || StringUtils.isNotBlank(request.getParameter("forum")) || StringUtils.isNotBlank(request.getParameter("address"))) {
            return true;
        }
        return false;
    }

    public ApiData saveoptions(HttpServletRequest request, ApiData apiData) {
        if (StringUtils.isNotBlank(apiData.getMessage()) && apiData.getMessage().contains("成功")) {
            if (StringUtils.isNotBlank(request.getParameter("loginName"))) {
                User tempUser = systemService.getUserByLoginName(request.getParameter("loginName"));
                if (tempUser != null) {
                    HashMap<String, String> mapOptions = new HashMap<>();
                    mapOptions.put("sex", request.getParameter("sex"));
                    mapOptions.put("type", request.getParameter("type"));
                    mapOptions.put("number", request.getParameter("number"));
                    if (request.getParameter("audienceNum") != null) {
                        mapOptions.put("audienceNum", request.getParameter("audienceNum"));
                    }
                    mapOptions.put("companyName", request.getParameter("companyName"));
                    mapOptions.put("joinRole", request.getParameter("joinRole"));
                    mapOptions.put("joinForum", request.getParameter("joinForum"));
                    mapOptions.put("address", request.getParameter("address"));
                    String optionsContent = JsonMapper.toJsonString(mapOptions);

                    Options options = new Options(tempUser.getId());
                    options.setContent(optionsContent);
                    options.setTitle("建博网注册用户：" + request.getParameter("loginName"));
                    options.setIsNewRecord(true);
                    optionsService.save(options);
                    apiData.setMessage("注册用户'" + request.getParameter("loginName") + "'成功,请登录。");
                    return apiData;

                } else {
                    apiData.setMessage("注册用户'" + request.getParameter("loginName") + "'失败,请重新注册。");
                    return apiData;
                }
            } else {
                apiData.setMessage("登录名不能为空。");
                return apiData;
            }
        }
        return apiData;
    }

    /**
     * 验证登录名是否有效
     *
     * @param loginName
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "checkLoginName")
    public String checkLoginName(String loginName) {
        if (loginName != null && systemService.getUserByLoginName(loginName) == null) {
            return "true";
        }
        return "false";
    }

    @RequestMapping(value = "useroption/save")
    public ApiData saveUserAndOptions(HttpServletRequest request) {
        ApiData apiData = new ApiData();
        HashMap<String, String> optionsMap = new HashMap<>();
        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String loginName = request.getParameter("loginName");
        String sex = request.getParameter("sex");
        String address = request.getParameter("address");
        String type = request.getParameter("type");
        String companyName = request.getParameter("companyName");
        String number = request.getParameter("number");
        String audienceNum = request.getParameter("audienceNum");
        String joinRole = request.getParameter("joinRole");
        String joinForum = request.getParameter("joinForum");

        User user = null;
        Options options = null;
        if (StringUtils.isNotBlank(id)) {
            user = systemService.getUser(id);
            options = optionsService.get(id);
        }
        if (user == null) {
            apiData.setMessage("用户信息异常");
            return apiData;
        }

        if (StringUtils.isNotBlank(name) || StringUtils.isNotBlank(password) || StringUtils.isNotBlank(phone) || StringUtils.isNotBlank(email) || StringUtils.isNotBlank(loginName)) {
            user.setName(name);
            user.setPhone(phone);
            user.setEmail(email);
            if (StringUtils.isNotBlank(password) && !password.equals(user.getPassword())) {
                systemService.updatePasswordById(user.getId(), user.getLoginName(), password);
            }
            systemService.updateUserInfo(user);

            optionsMap.put("sex", sex);
            optionsMap.put("address", address);
            optionsMap.put("type", type);
            optionsMap.put("companyName", companyName);
            if (options != null) {
                Map<String, String> oriOptions = JsonMapper.fromJsonString(options.getContent(), Map.class);
                optionsMap.put("joinRole", oriOptions.get("joinRole") != null ? oriOptions.get("joinRole").toString() : "");
                optionsMap.put("joinForum", oriOptions.get("joinForum") != null ? oriOptions.get("joinRole").toString() : "");
                optionsMap.put("number", oriOptions.get("number") != null ? oriOptions.get("joinRole").toString() : "");
                options.setContent(JsonMapper.toJsonString(optionsMap));
                optionsService.save(options);
            } else {
                options = new Options(user.getId());
                options.setTitle("建博网注册用户：" + request.getParameter("loginName"));
                options.setIsNewRecord(true);
                options.setContent(JsonMapper.toJsonString(optionsMap));
                optionsService.save(options);
            }
            apiData.setMessage("保存用户'" + loginName + "'成功");
            return apiData;
        }

        if (StringUtils.isNotBlank(joinRole) || StringUtils.isNotBlank(joinForum) || StringUtils.isNotBlank(number) && Integer.parseInt(number) > 0) {
            optionsMap.put("joinRole", joinRole);
            optionsMap.put("joinForum", joinForum);
            optionsMap.put("number", number);
            String msg = "";
            if (options != null) {
                Map<String, String> oriOptions = JsonMapper.fromJsonString(options.getContent(), Map.class);
                optionsMap.put("companyName", oriOptions.get("companyName") != null ? oriOptions.get("companyName").toString() : "");
                optionsMap.put("sex", oriOptions.get("sex") != null ? oriOptions.get("sex").toString() : "");
                optionsMap.put("type", oriOptions.get("type") != null ? oriOptions.get("type").toString() : "");
                optionsMap.put("address", oriOptions.get("address") != null ? oriOptions.get("address").toString() : "");
                options.setContent(JsonMapper.toJsonString(optionsMap));
                optionsService.save(options);
            } else {
                options = new Options(user.getId());
                options.setTitle("建博网注册用户#论坛会议报名人员：" + user.getLoginName());
                options.setIsNewRecord(true);
                options.setContent(JsonMapper.toJsonString(optionsMap));
                optionsService.save(options);
            }
            if (joinRole.equals("1") && StringUtils.isNotBlank(joinForum) && Integer.parseInt(number) > 0) {
                apiData.setMessage("您已经成功报名！");
            } else {
                apiData.setMessage("您报名失败或成功取消报名！");
            }
            return apiData;
        }
        apiData.setMessage("提交失败!");
        return apiData;
    }

}
