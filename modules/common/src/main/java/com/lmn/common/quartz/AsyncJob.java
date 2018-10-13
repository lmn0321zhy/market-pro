package com.lmn.common.quartz;

import com.lmn.common.quartz.dto.QuartzJobDTO;
import com.lmn.common.quartz.dto.QuartzResultDTO;
import com.lmn.common.quartz.impl.QuartzJobServiceImpl;
import com.lmn.common.quartz.impl.QuartzResultServiceImpl;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Job实现类  无状态
 * 若此方法上一次还未执行完，而下一次执行时间轮到时则该方法也可并发执行
 */

public class AsyncJob implements Job {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private QuartzJobServiceImpl quartzJobService;
    @Autowired
    private QuartzResultServiceImpl quartzResultService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDetail job = context.getJobDetail();
        JobKey key = job.getKey();
        String jobIdentity = "scheduleJob" + key.getGroup() + "_" + key.getName();
        Trigger trigger = context.getTrigger();
        QuartzJobDTO scheduleJob = (QuartzJobDTO) context.getMergedJobDataMap().get(jobIdentity);
        logger.info("运行任务名称 = [" + scheduleJob + "]");
        try {
            QuartzResultDTO result = QuartzJobUtils.invokMethod(scheduleJob);
            scheduleJob.setNextTime(trigger.getNextFireTime());
            scheduleJob.setPreviousTime(trigger.getPreviousFireTime());
            quartzJobService.update(scheduleJob);
            // 写入运行结果
            quartzResultService.insert(result);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
