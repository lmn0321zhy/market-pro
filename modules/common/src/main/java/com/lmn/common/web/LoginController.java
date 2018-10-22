package com.lmn.common.web;

import com.lmn.common.base.ApiData;
import com.lmn.common.base.BaseController;
import com.lmn.common.entity.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by lmn on 2018-10-22.
 */
@Controller
public class LoginController extends BaseController{
    @RequestMapping("/login")
    public ApiData login(User user){
        ApiData jsonObject = new ApiData();
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getName(), user.getPassword());
        try {
            subject.login(token);
            jsonObject.setData(subject.getSession().getId());
            jsonObject.setMessage("登录成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
