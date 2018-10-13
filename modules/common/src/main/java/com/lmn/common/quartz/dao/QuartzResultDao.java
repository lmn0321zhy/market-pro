package com.lmn.common.quartz.dao;

import com.lmn.common.persistence.CrudDao;
import com.lmn.common.quartz.dto.QuartzResultDTO;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by lmn on 2018-10-11.
 */
@Mapper
public interface QuartzResultDao extends CrudDao<QuartzResultDTO> {
}
