package com.lmn.common.quartz;


import com.lmn.common.quartz.dto.QuartzJobDTO;
import com.lmn.common.quartz.dto.QuartzResultDTO;
import com.lmn.common.quartz.impl.QuartzResultServiceImpl;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 *  Job有状态实现类，不允许并发执行
 * 	若一个方法一次执行不完下次轮转时则等待该方法执行完后才执行下一次操作
 * 	主要是通过注解：@DisallowConcurrentExecution
 */
@DisallowConcurrentExecution
public class QuartzJobFactoryDisallowConcurrentExecution implements Job {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private  QuartzJobService quartzJobService;
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
            quartzJobService.updateByIdAndTime(scheduleJob);
            // 写入运行结果
            quartzResultService.insert(result);
        } catch (Exception e) {
            QuartzResultDTO result=new QuartzResultDTO();
            quartzResultService.insert(result);
            logger.error(e.getMessage(), e);
        }
    }

}
