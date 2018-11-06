package com.lmn.common.web;

import com.lmn.common.base.ApiData;
import com.lmn.common.base.BaseController;
import com.lmn.common.entity.User;
import com.lmn.common.service.UserService;
import com.lmn.common.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lmn on 2018-10-22.
 */
@RestController
@Api("swaggerTestController相关api")
public class LoginController extends BaseController{
    @Autowired
    private UserService userService;


    /**
     * 登录失败，真正登录的POST请求由Filter完成
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ApiOperation(value = "登录接口")
    public ApiData loginFail(HttpServletRequest request, HttpServletResponse response, Model model) {
        Subject subject = SecurityUtils.getSubject();
        Object principal = subject.getPrincipal();
        Map data = new HashMap<>();
        ApiData apiData = new ApiData<>(data);

        String username = WebUtils.getCleanParam(request, FormAuthenticationFilter.DEFAULT_USERNAME_PARAM);
        String exception = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
//        String message = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_MESSAGE_PARAM);

//        if (StringUtils.isBlank(message) || StringUtils.equals(message, "null")) {
//            message = "用户或密码错误, 请重试.";
//        }
//        apiData.setMessage(message);
        apiData.setAuthenticate("false");
        return apiData;
    }



    @RequestMapping("/registry")
    public ApiData registry(User user){
        ApiData apiData = userService.registry(user);
        return apiData;
    }


}
