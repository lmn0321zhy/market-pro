package com.lmn.common.sys.service;

import cn.wenwuyun.common.service.CrudService;
import cn.wenwuyun.common.utils.CacheUtils;
import cn.wenwuyun.modules.sys.dao.DictDao;
import cn.wenwuyun.modules.sys.entity.Dict;
import cn.wenwuyun.modules.sys.utils.DictUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 字典Service
 */
@Component
public class DictService extends CrudService<DictDao, Dict> {
	
	/**
	 * 查询字段类型列表
	 * @return
	 */
	public List<String> findTypeList(){
		return dao.findTypeList(new Dict());
	}

	public void save(Dict dict) {
		super.save(dict);
		CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
	}

	public void delete(Dict dict) {
		super.delete(dict);
		CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
	}

	public Dict getDict(Dict dict){return dao.getDict(dict);}

	/**
	 * 根据type 模糊查询
	 * @param type
	 * @return
     */
	public List<Dict> findAllListByVague(String type){
		return dao.findAllListByVague(type);
	}

    public Dict getDictByValue(Dict d) { return  dao.getDictByValue(d);
    }
}
