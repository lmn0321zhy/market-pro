package com.lmn.common.quartz;

import org.quartz.JobExecutionException;

/**
 * Created by lmn on 2018-10-11.
 */
public interface BaseJob  {
    void execute() throws JobExecutionException;
}
