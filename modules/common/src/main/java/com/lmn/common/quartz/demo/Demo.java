package com.lmn.common.quartz.demo;

import com.lmn.common.base.BaseJob;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lmn on 2018-10-12.
 */
public class Demo implements BaseJob{
    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public void execute() throws JobExecutionException {
        logger.info("quzrtz----TEST");
    }
}
