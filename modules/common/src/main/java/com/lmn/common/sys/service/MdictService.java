package com.lmn.common.sys.service;

import cn.wenwuyun.modules.sys.dao.MdictDao;
import cn.wenwuyun.modules.sys.entity.Mdict;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 多级字典Service
 *
 * @version 2014-05-16
 */
@Service
public class MdictService extends TreeService<MdictDao, Mdict> {

    public List<Mdict> findAll(Mdict mdict) {
        return dao.findAllList(mdict);
    }

    public Mdict get(String id) {
        return super.get(id);
    }

    public void save(Mdict Mdict) {
        super.save(Mdict);
    }

    public void delete(Mdict Mdict) {
        super.delete(Mdict);
    }

    public List<Mdict> findByName(Mdict Mdict) {
        return dao.findAllList(Mdict);
    }

    public List<Mdict> findByParentId(Mdict Mdict) {
        return dao.findByParentId(Mdict);
    }

    public List<Mdict> findAllChildrenByParentId(Mdict Mdict) {
        return dao.findAllChildrenByParentId(Mdict);
    }

    public List<Mdict> findAllListByStation(String stationId) {
        return dao.findAllListByStation(stationId);
    }


}
