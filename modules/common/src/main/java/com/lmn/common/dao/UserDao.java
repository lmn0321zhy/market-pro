package com.lmn.common.dao;

import com.lmn.common.base.BaseDao;
import com.lmn.common.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by lmn on 2018-10-22.
 */
@Mapper
public interface UserDao extends BaseDao<User> {
}
