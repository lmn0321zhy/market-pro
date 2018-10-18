package com.lmn.common.shiro;

import com.lmn.common.base.CrudService;
import com.lmn.common.dao.MetaDao;
import com.lmn.common.entity.Meta;
import com.lmn.common.entity.User;

/**
 * Created by lmn on 2018-10-18.
 */
public class UserService  extends CrudService<UserDao, User> {
    public User findByName(String username) {
        return new User();
    }
}
