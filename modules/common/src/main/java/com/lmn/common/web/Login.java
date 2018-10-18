package com.lmn.common.web;

import com.lmn.common.base.ApiData;
import com.lmn.common.base.BaseController;
import com.lmn.common.entity.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.stereotype.Controller;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by lmn on 2018-10-18.
 */
@Controller
public class Login extends BaseController{

    public ApiData login(User user){
        ApiData data = new ApiData();
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(user.getLoginName(), user.getPassword());
            SecurityUtils.getSubject().login(token);
        } catch (Exception e) {

        }
        return data;
    }

}
