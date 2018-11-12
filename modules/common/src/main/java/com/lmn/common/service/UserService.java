package com.lmn.common.service;

import com.lmn.common.base.ApiData;
import com.lmn.common.base.BaseDao;
import com.lmn.common.base.BaseService;
import com.lmn.common.dao.UserDao;
import com.lmn.common.entity.User;
import com.lmn.common.utils.ShiroUtils;
import lombok.Data;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lmn on 2018-10-22.
 */
@Service
@Data
public class UserService extends BaseService<UserDao, User> {
    @Autowired
    private UserDao userDao;

    public User findByName(String name) {
        User user = new User();
        user.setLoginName(name);
        return userDao.getByEntity(user);
    }

    public ApiData registry(User user) {
        ApiData apiData = new ApiData();
        Subject subject = SecurityUtils.getSubject();
        Object principal = subject.getPrincipal();
        // 如果已经登录，则跳转到前台首页
        if (principal != null) {
            apiData.setRedirect("/");
            return apiData;
        }
        if (checkLoginName(user.getLoginName())) {
            apiData.setMessage("登录名已存在,请使用新的用户名");
        } else {
            user.setPassword(ShiroUtils.entryptPassword(user.getPassword()));
            save(user);
            apiData.setMessage("注册用户'" + user.getLoginName() + "'成功,请登录。");
            return apiData;
        }
        //}
        return apiData;
    }

    private boolean checkLoginName(String loginName) {
        User user = findByName(loginName);
        if (user != null) {
            return true;
        }
        return false;
    }
}
