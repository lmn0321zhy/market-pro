package com.lmn.common.service;

import com.lmn.common.base.BaseDao;
import com.lmn.common.base.BaseService;
import com.lmn.common.dao.UserDao;
import com.lmn.common.entity.User;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lmn on 2018-10-22.
 */
@Service
@Data
public class UserService extends BaseService<UserDao,User> {
    @Autowired
    private UserDao userDao;
    public User findByName(String name) {
        User user=new User();
        user.setName(name);
        return userDao.getByEntity(user);
    }
}
