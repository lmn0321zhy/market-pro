package com.lmn.common.quartz;

import com.lmn.common.utils.QuartzJobUtils;
import com.lmn.common.quartz.dto.QuartzJobDTO;
import com.lmn.common.quartz.dto.QuartzResultDTO;
import com.lmn.common.quartz.service.QuartzJobService;
import com.lmn.common.quartz.service.QuartzResultService;
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
    private QuartzJobService quartzJobService;
    @Autowired
    private QuartzResultService quartzResultService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDetail job = context.getJobDetail();
        JobKey key = job.getKey();
        String jobIdentity = "scheduleJob" + key.getGroup() + "_" + key.getName();
        Trigger trigger = context.getTrigger();
        QuartzJobDTO scheduleJob = (QuartzJobDTO) context.getMergedJobDataMap().get(jobIdentity);
        logger.info("运行任务名称 = [" + scheduleJob + "]");
        QuartzResultDTO result = QuartzJobUtils.invokMethod(scheduleJob);
        scheduleJob.setNextTime(trigger.getNextFireTime());
        scheduleJob.setPreviousTime(trigger.getPreviousFireTime());
        try {
            quartzJobService.update(scheduleJob);
            // 写入运行结果
            quartzResultService.save(result);
            logger.info("运行结束任务名称 = [" + scheduleJob + "]");
        }catch (Exception e){
            logger.info("运行任务名称 = [" + scheduleJob + "]出现异常："+e.getMessage());
        }
    }
}
