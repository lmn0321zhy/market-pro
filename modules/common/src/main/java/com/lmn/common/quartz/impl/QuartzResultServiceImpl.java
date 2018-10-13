package com.lmn.common.quartz.impl;

import com.lmn.common.quartz.dto.QuartzResultDTO;
import com.lmn.common.quartz.dao.QuartzResultDao;
import com.lmn.common.quartz.QuartzResultService;
import com.lmn.common.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lmn on 2018-10-11.
 */
@Service
public class QuartzResultServiceImpl extends CrudService<QuartzResultDao, QuartzResultDTO> implements QuartzResultService {
    @Autowired
    private QuartzResultDao quartzResultDao;

    public int insert(QuartzResultDTO dto){
        return quartzResultDao.insert(dto);
    }
}
