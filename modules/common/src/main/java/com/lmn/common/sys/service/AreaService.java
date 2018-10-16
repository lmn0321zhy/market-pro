package com.lmn.common.sys.service;

import cn.wenwuyun.common.utils.Collections3;
import cn.wenwuyun.common.utils.StringUtils;
import cn.wenwuyun.modules.sys.dao.AreaDao;
import cn.wenwuyun.modules.sys.entity.Area;
import cn.wenwuyun.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 区域Service
 */
@Component
public class AreaService extends TreeService<AreaDao, Area> {

    public List<Area> findAll() {
        return UserUtils.getAreaList();
    }

    public List<Area> findList(Area area) {
        @SuppressWarnings("unchecked")
        List<Area> areaList = (List<Area>) UserUtils.getCache(UserUtils.CACHE_AREA_LIST + "_" + area.getParent().getId());
        if (areaList == null) {
            areaList = super.findList(area);
            UserUtils.putCache(UserUtils.CACHE_AREA_LIST + "_" + area.getParent().getId(), areaList);
        }
        return areaList;
    }

    public List<Area> findAllArea(Area area) {
        return dao.findAllList(area);
    }

    public void save(Area area) {
        super.save(area);
        UserUtils.removeCache(UserUtils.CACHE_AREA_LIST);
    }

    public void delete(Area area) {
        super.delete(area);
        UserUtils.removeCache(UserUtils.CACHE_AREA_LIST);
    }

    public Area getByName(Area area) {
        return dao.getByName(area);
    }

    public List<Area> findByParentId(Area area) {
        return dao.findByParentId(area);
    }


    public Area findAreaByName(String city, int recursion) {
        String name = "";
        if (city.indexOf("市") != -1 || city.indexOf("县") != -1 || city.indexOf("州") != -1 || (city.indexOf("区") != -1) || city.indexOf("旗") != -1) {
            if (city.indexOf("县") != -1) {
                if (city.indexOf("市") != -1 && city.indexOf("地区") == -1 && city.indexOf("市") < city.indexOf("县")) {
                    name = city.substring(city.lastIndexOf("市") + 1, city.lastIndexOf("县") + 1);
                } else if (city.indexOf("地区") != -1) {
                    name = city.substring(city.lastIndexOf("地区") + 2, city.lastIndexOf("县") + 1);
                } else if (city.indexOf("自治州") != -1) {
                    name = city.substring(city.lastIndexOf("自治州") + 3, city.lastIndexOf("县") + 1);
                } else if (city.indexOf("州") != -1) {
                    if (city.lastIndexOf("县") - 1 == city.lastIndexOf("州") && city.indexOf("州") != city.lastIndexOf("州")) {
                        int index = city.substring(0, city.lastIndexOf("州")).lastIndexOf("州");
                        name = city.substring(index + 1, city.lastIndexOf("县") + 1);
                    } else {
                        name = city.substring(city.lastIndexOf("州") + 1, city.lastIndexOf("县") + 1);
                    }
                }
            } else if (city.indexOf("市") != -1) {
                if (city.indexOf("区") != -1 && city.lastIndexOf("区") > city.lastIndexOf("市")) {
                    name = city.substring(city.lastIndexOf("市") + 1, city.lastIndexOf("区") + 1);
                } else if (city.indexOf("市") != city.lastIndexOf("市") && city.indexOf("市") + 1 != city.lastIndexOf("市")) {//避免抚顺市市辖区之类出现
                    name = city.substring(city.indexOf("市") + 1, city.lastIndexOf("市") + 1);
                } else if (city.indexOf("自治州") != -1 || city.indexOf("自治区") != -1) {
                    name = city.indexOf("地区") == -1 ? city.substring(city.lastIndexOf("自治州") + 3, city.lastIndexOf("市") + 1) : city.substring(city.lastIndexOf("地区") + 2, city.lastIndexOf("市") + 1);
                } else if (city.indexOf("州") != -1) {
                    if (city.indexOf("州") != city.lastIndexOf("州") && !"州".equals(city.charAt(city.lastIndexOf("市") - 1))) {
                        int index = city.substring(0, city.lastIndexOf("州")).lastIndexOf("州");
                        name = city.substring(index + 1, city.lastIndexOf("市") + 1);
                    } else if (!"州".equals(String.valueOf(city.charAt(city.lastIndexOf("市") - 1)))) {
                        name = city.substring(city.lastIndexOf("州") + 1, city.lastIndexOf("市") + 1);
                    }
                } else if (city.indexOf("省") != -1) {
                    name = city.substring(city.indexOf("省") + 1, city.indexOf("市") + 1);
                }
            } else if (city.indexOf("自治州") != -1 || city.indexOf("自治区") != -1) {
                if (city.indexOf("地区") != -1 && city.indexOf("自治") < city.indexOf("地区")) {
                    name = city.substring(city.indexOf("自治") + 3, city.lastIndexOf("地区") + 2);
                } else if (city.indexOf("省") != -1) {
                    name = city.substring(city.indexOf("省") + 1, city.indexOf("自治") + 3);
                }
            }
            if (StringUtils.isBlank(name)) {
                if (city.indexOf("盟") != -1 && city.indexOf("旗") != -1) {
                    name = city.substring(city.indexOf("盟") + 1, city.lastIndexOf("旗") + 1);
                } else if (city.indexOf("州") != -1 && !"州".equals(String.valueOf(city.charAt(city.lastIndexOf("市") - 1)))) {
                    name = city.substring(city.indexOf("州") + 1);
                } else if (city.indexOf("省") != -1) {
                    name = city.substring(city.indexOf("省") + 1);
                } else {
                    name = city;
                }
            }
        }

        List<Area> list = dao.getByName(name);
        Area area = null;
        if (!Collections3.isEmpty(list)) {
            if (list.size() == 1) {
                area = list.get(0);
            } else {
                for (Area a : list) {
                    if (city.indexOf(a.getParent().getName()) != -1) {
                        area = a;
                        break;
                    }
                }
            }
        } else {
            if (recursion == 0) {
                if (city.indexOf("县") + 1 == city.length())
                    return findAreaByName(city.substring(0, city.length() - 1) + "市", 1);
                if (city.indexOf("市") + 1 == city.length())
                    return findAreaByName(city.substring(0, city.length() - 1) + "县", 1);
                if (city.indexOf("市") != -1)
                    return findAreaByName(city.substring(city.indexOf("市") - 2, city.indexOf("市") + 1), 2);
                if (city.indexOf("省") != -1 && (city.indexOf("自治州") != -1 || city.indexOf("自治区") != -1)) {
                    return findAreaByName(city.substring(0, city.indexOf("自治") + 3), 2);
                }
            } else if (recursion == 1) {
                if (city.indexOf("省") != -1 && (city.indexOf("自治州") != -1 || city.indexOf("自治区") != -1)) {
                    return findAreaByName(city.substring(0, city.indexOf("自治") + 3), 2);
                }
                if (city.indexOf("市") != -1)
                    return findAreaByName(city.substring(city.indexOf("市") - 2, city.indexOf("市") + 1), 2);
            }
        }
        return area;
    }


    public List<Area> findByStations() {
        Area area = new Area();
        area.setCurrentUser(UserUtils.getUser());
        return dao.findByStations(area);
    }
    public Area getProvinceById(Area area){
        return dao.getProvinceById(area);
    };

}
