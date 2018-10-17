package com.lmn.common.dao;


import com.lmn.common.base.CrudDao;
import com.lmn.common.sys.entity.Options;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户配置信息表DAO接口
 * @author 李顺兴
 * @version 2016-01-14
 */
public interface OptionsDao extends CrudDao<Options> {

    Options findOptionByTitle(Map map);

    Options findOptionByTitleAndUer(Map map);

    List<HashMap<String,String>> findUserAndOptions(Options options);
}