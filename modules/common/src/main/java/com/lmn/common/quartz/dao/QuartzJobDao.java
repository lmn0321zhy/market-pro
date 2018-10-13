package com.lmn.common.quartz.dao;

import com.lmn.common.base.CrudDao;
import com.lmn.common.quartz.dto.QuartzJobDTO;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by lmn on 2018-10-10.
 */
@Mapper
public interface QuartzJobDao extends CrudDao<QuartzJobDTO> {

}
