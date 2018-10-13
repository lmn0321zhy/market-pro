package com.lmn.common.service;

import com.lmn.common.persistence.BaseEntity;
import com.lmn.common.persistence.CrudDao;
import com.lmn.common.persistence.Paging;
import com.lmn.common.utils.Collections3;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Service基类
 */
public abstract class CrudService<D extends CrudDao<T>, T extends BaseEntity<T>> extends BaseService {

    /**
     * 持久层对象
     */
    @Autowired
    protected D dao;

    /**
     * 获取单条数据
     *
     * @param id
     * @return
     */
    public T get(String id) {
        return dao.get(id);
    }

    /**
     * 获取单条数据
     *
     * @param entity
     * @return
     */
    public T get(T entity) {
        return dao.get(entity);
    }

    /**
     * 查询列表数据
     *
     * @param entity
     * @return
     */
    public List<T> findList(T entity) {
        return dao.findList(entity);
    }

    /**
     * 查询分页数据
     *
     * @param request
     * @param entity
     * @return
     */
    public PageInfo<T> findPage(HttpServletRequest request, T entity) {
        Paging paging = new Paging(request);
        return findPage(paging, entity);
    }


    /**
     * 查询分页数据
     *
     * @param paging
     * @param entity
     * @return
     */
    public PageInfo<T> findPage(Paging paging, T entity) {
        PageHelper.startPage(paging.getPageNum(), paging.getPageSize(), paging.getTotal() <= 0);
        if (StringUtils.isNotBlank(paging.getOrderBy())) {
            PageHelper.orderBy(paging.getOrderBy());
        }
        List<T> list = dao.findList(entity);
        return new PageInfo<>(list);
    }


    /**
     * 保存数据（插入或更新）
     *
     * @param entity
     */
    public void save(T entity) {
        if (entity.getIsNewRecord()) {
            entity.preInsert();
            dao.insert(entity);
        } else {
            entity.preUpdate();
            dao.update(entity);
        }
    }


    /**
     * 批量保存数据（插入或更新）
     *
     * @param list
     */

    public Integer saveBatch(List<T> list) {
        if (Collections3.isEmpty(list)) return 0;
        int j = 0;
        for (int i = 0; i <= list.size(); i++) {
            if (i != list.size()) {
                T entity = list.get(i);
                if (entity.getIsNewRecord()) {
                    entity.preInsert();
                }
            }
            if (i > 0 && i % 1000 == 0 || i == list.size()) {
                dao.saveBatch(list.subList(j, i));
                j = i;
            }
        }
        list.clear();
        return j;
    }
    /**
     * 更新数据
     *
     * @param entity
     * @return
     */
    public int update(T entity) {
        return dao.update(entity);
    }
    /**
     * 删除数据
     *
     * @param entity
     */
    public void delete(T entity) {
        dao.delete(entity);
    }

}
