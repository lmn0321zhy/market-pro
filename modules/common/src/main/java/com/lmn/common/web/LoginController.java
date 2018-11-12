package com.lmn.common.web;

import com.lmn.common.base.ApiData;
import com.lmn.common.base.BaseController;
import com.lmn.common.entity.User;
import com.lmn.common.service.UserService;
import com.lmn.common.utils.StringUtils;
import io.swagger.annotations.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
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
@Api("登录相关的接口")
public class LoginController extends BaseController{
    @Autowired
    private UserService userService;

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ApiOperation(value = "登录接口")
    public ApiData login(@ApiParam(name="用户对象",value="传入json格式",required=true)User user) {
        Subject subject = SecurityUtils.getSubject();
        ApiData apiData = new ApiData<>();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getLoginName(), user.getPassword());
        try {
            subject.login(token);
            user = (User) subject.getPrincipal();
            apiData.setMessage("登录成功!");
            apiData.setData(user);
        }catch (Exception e){
            apiData.setMessage("登录失败!");
            apiData.setData(null);
        }
        return apiData;
    }



    @PostMapping("/registry")
    public ApiData registry(User user){
        ApiData apiData = userService.registry(user);
        return apiData;
    }


}
