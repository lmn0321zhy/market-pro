package com.lmn.common.sys.service;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lmn.common.dao.OfficeDao;
import com.lmn.common.sys.entity.Office;
import com.lmn.common.sys.utils.UserUtils;
import com.lmn.common.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 机构Service
 */
@Component
public class OfficeService extends TreeService<OfficeDao, Office> {

    public List<Office> findAll() {
        return UserUtils.getOfficeList();
    }
    public List<Office> findAllList() {
        Office office=new Office();
        return dao.findAllList(office);
    }
    public List<Office> findList(Boolean isAll) {
        if (isAll != null && isAll) {
            return UserUtils.getOfficeAllList();
        } else {
            return UserUtils.getOfficeList();
        }
    }


    public List<Office> findList(Office office) {
        if (office != null) {
            //office.setParentIds("%,"+office.getParentIds() + ",%");
            office.setParentIds(office.getParentIds());
            return dao.findByParentIdsLike(office);
        }
        return new ArrayList<Office>();
    }

    public Map getChildDept(String parentId){
        if(StringUtils.isBlank(parentId)){
            return null;
        }
        Office office=new Office();
        office.setParentIds(parentId);
        List<Office> lists=this.findList(office);
        List<String> listStr= Lists.newArrayList();
        String str="";
        for (int i=0;i<lists.size();i++){
            listStr.add(lists.get(i).getName());
            if(i==lists.size()-1){
                str+=lists.get(i).getName();
            }else{
                str+=lists.get(i).getName()+",";
            }
        }
        Map map= Maps.newHashMap();
        map.put("childDeptList",listStr);
        map.put("childDeptStr",str);
        return map;
    }

    public void save(Office office) {
        super.save(office);
        UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
    }

    public void delete(Office office) {
        super.delete(office);
        UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
    }

}
