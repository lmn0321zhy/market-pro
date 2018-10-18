package com.lmn.common.dao;


import com.lmn.common.base.CrudDao;
import com.lmn.common.entity.Menu;

import java.util.HashMap;
import java.util.List;

/**
 * 菜单DAO接口
 */
public interface MenuDao extends CrudDao<Menu> {

    public List<Menu> findByParentIdsLike(Menu menu);

    public List<HashMap<String,Object>> findByParentId(Menu menu);

    public List<Menu> findByUserId(Menu menu);
    public List<HashMap<String,Object>> findByUserIdAndParentId(Menu menu);

    public int updateParentIds(Menu menu);

    public int updateShow(Menu menu);

    public int updateSort(Menu menu);

}
