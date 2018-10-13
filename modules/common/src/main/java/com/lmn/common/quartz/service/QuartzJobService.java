package com.lmn.common.quartz.service;

import com.lmn.common.quartz.AsyncJob;
import com.lmn.common.quartz.QuartzJobFactoryDisallowConcurrentExecution;
import com.lmn.common.quartz.dao.QuartzJobDao;
import com.lmn.common.quartz.dto.QuartzJobDTO;
import com.lmn.common.base.CrudService;
import com.lmn.common.utils.QuartzJobUtils;
import lombok.Data;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by lmn on 2018-10-10.
 */
@Data
@Service
public class QuartzJobService extends CrudService<QuartzJobDao, QuartzJobDTO> {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private QuartzJobDao quartzJobDao;

    /**
     * 创建job
     * @param jobName
     * @param jobGroup
     * @param scheduler
     * @param trigger
     * @return
     * @throws SchedulerException
     */
    private QuartzJobDTO createJob(String jobName, String jobGroup, Scheduler scheduler, Trigger trigger)
            throws SchedulerException {
        QuartzJobDTO job=new QuartzJobDTO();
        job.setJobName(jobName);
        job.setJobGroup(jobGroup);
        job.setDescription("触发器:" + trigger.getKey());
        job.setNextTime(trigger.getNextFireTime());
        job.setPreviousTime(trigger.getPreviousFireTime());

        Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
        job.setJobStatus(triggerState.name());

        if(trigger instanceof CronTrigger) {
            CronTrigger cronTrigger = (CronTrigger)trigger;
            String cronExpression = cronTrigger.getCronExpression();
            job.setCronExpression(cronExpression);
        }
        return job;
    }


    /**
     * 获取单个任务
     * @param jobName
     * @param jobGroup
     * @return
     * @throws SchedulerException
     */
    public QuartzJobDTO getJob(String jobName,String jobGroup) throws SchedulerException {
        QuartzJobDTO job = null;
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        if (null != trigger) {
            job = createJob(jobName, jobGroup, scheduler, trigger);
        }

        return job;
    }
    /**
     * 获取所有任务
     * @return
     * @throws SchedulerException
     */
    public List<QuartzJobDTO> getAllJobs() throws SchedulerException{
        GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
        Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
        List<QuartzJobDTO> jobList = new ArrayList<QuartzJobDTO>();
        List<? extends Trigger> triggers;
        QuartzJobDTO job;
        for (JobKey jobKey : jobKeys) {
            triggers = scheduler.getTriggersOfJob(jobKey);
            for (Trigger trigger : triggers) {
                job = createJob(jobKey.getName(), jobKey.getGroup(), scheduler, trigger);
                jobList.add(job);
            }
        }

        return jobList;
    }

    /**
     * 所有正在运行的job
     *
     * @return
     * @throws SchedulerException
     */
    
    public List<QuartzJobDTO> getRunningJob() throws SchedulerException {
        List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
        List<QuartzJobDTO> jobList = new ArrayList<QuartzJobDTO>(executingJobs.size());
        QuartzJobDTO job;
        JobDetail jobDetail;
        JobKey jobKey;

        for (JobExecutionContext executingJob : executingJobs) {
            jobDetail = executingJob.getJobDetail();
            jobKey = jobDetail.getKey();

            job = createJob(jobKey.getName(), jobKey.getGroup(), scheduler, executingJob.getTrigger());
            jobList.add(job);
        }

        return jobList;
    }

    /**
     * 添加任务
     *
     * @param job
     * @throws SchedulerException
     */
    
    public boolean addJob(QuartzJobDTO job) throws SchedulerException {
        if(job == null || !QuartzJobDTO.STATUS_RUNNING.equals(job.getJobStatus())) {
            return false;
        }
        String jobName = job.getJobName();
        String jobGroup = job.getJobGroup();
        if(!QuartzJobUtils.isValidExpression(job.getCronExpression())) {
            logger.error("时间表达式错误（"+jobName+","+jobGroup+"）, "+job.getCronExpression());
            return false;
        } else {
            // 任务名称和任务组设置规则：    // 名称：task_1 ..    // 组 ：group_1 ..
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName,	jobGroup);
            Trigger trigger = scheduler.getTrigger(triggerKey);
            // 不存在，创建一个
            if (null == trigger) {
                // 表达式调度构建器
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
                // 按新的表达式构建一个新的trigger
                trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
                        .startAt(job.getStartTime()==null ? (new Date()) : job.getStartTime()) // 设置job不早于这个时间进行运行,和调用trigger的setStartTime方法效果一致
                        .withSchedule(scheduleBuilder).build();

                //是否允许并发执行
                JobDetail jobDetail = getJobDetail(job);
                // 将 job 信息存入数据库
                job.setStartTime(trigger.getStartTime());
                job.setNextTime(trigger.getNextFireTime());
                job.setPreviousTime(trigger.getPreviousFireTime());
                quartzJobDao.update(job);
                jobDetail.getJobDataMap().put(getJobIdentity(job), job);
                scheduler.scheduleJob(jobDetail, trigger);
            } else { // trigger已存在，则更新相应的定时设置
                // 更新 job 信息到数据库
                job.setStartTime(trigger.getStartTime());
                job.setNextTime(trigger.getNextFireTime());
                job.setPreviousTime(trigger.getPreviousFireTime());
                 quartzJobDao.update(job);
                getJobDetail(job).getJobDataMap().put(getJobIdentity(job), job);

                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
                // 按新的表达式构建一个新的trigger
                trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
                        .startAt(job.getStartTime()==null ? (new Date()) : job.getStartTime()) // 设置job不早于这个时间进行运行,和调用trigger的setStartTime方法效果一致
                        .withSchedule(scheduleBuilder).build();
                scheduler.rescheduleJob(triggerKey, trigger);
            }
        }
        return true;
    }

    private String getJobIdentity(QuartzJobDTO job) {
        return "scheduleJob"+(job.getJobGroup() +"_"+job.getJobName());
    }

    private JobDetail getJobDetail(QuartzJobDTO job) {
        Class<? extends Job> clazz = QuartzJobDTO.CONCURRENT_IS.equals(job.getIsConcurrent())
                ? AsyncJob.class : QuartzJobFactoryDisallowConcurrentExecution.class;
        JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(job.getJobName(), job.getJobGroup()).build();
        return jobDetail;
    }

    /**
     * 暂停任务
     * @param job
     * @return
     */
    
    @Transactional
    public boolean pauseJob(QuartzJobDTO job){
        JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
        boolean result;
        try {
            scheduler.pauseJob(jobKey);

            // 更新任务状态到数据库
            job.setJobStatus(QuartzJobDTO.STATUS_NOT_RUNNING);
            quartzJobDao.update(job);

            result = true;
        } catch (SchedulerException e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 恢复任务
     * @param job
     * @return
     */
    
    @Transactional
    public boolean resumeJob(QuartzJobDTO job){
        JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
        boolean result;
        try {
            logger.info("resume job : " + (job.getJobGroup() + "_" + job.getJobName()));
            TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
            // 表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
                    .startAt(job.getStartTime()==null ? (new Date()) : job.getStartTime()) // 设置job不早于这个时间进行运行,和调用trigger的setStartTime方法效果一致
                    .withSchedule(scheduleBuilder).build();
            scheduler.rescheduleJob(triggerKey, trigger);
            scheduler.resumeJob(jobKey);

            // 更新任务状态到数据库
            job.setJobStatus(QuartzJobDTO.STATUS_RUNNING);
            quartzJobDao.update(job);

            result = true;
        } catch (SchedulerException e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除任务
     */
    
    @Transactional
    public boolean deleteJob(QuartzJobDTO job){
        JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
        boolean result;
        try{
            scheduler.deleteJob(jobKey);

            // 更新任务状态到数据库
            job.setJobStatus(QuartzJobDTO.STATUS_DELETED);
            quartzJobDao.update(job);

            result = true;
        } catch (SchedulerException e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 立即执行一个任务
     * @param scheduleJob
     * @throws SchedulerException
     */
    
    public void startJob(QuartzJobDTO scheduleJob) throws SchedulerException{
        JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        scheduler.triggerJob(jobKey);
    }

    /**
     * 更新任务时间表达式
     * @param job
     * @throws SchedulerException
     */
    
    @Transactional
    public void updateCronExpression(QuartzJobDTO job) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
        //获取trigger，即在spring配置文件中定义的 bean id="myTrigger"
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        //表达式调度构建器
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
        //按新的cronExpression表达式重新构建trigger
        trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
        //按新的trigger重新设置job执行
        scheduler.rescheduleJob(triggerKey, trigger);

        // 更新 job 信息到数据库
        job.setStartTime(trigger.getStartTime());
        job.setNextTime(trigger.getNextFireTime());
        job.setPreviousTime(trigger.getPreviousFireTime());
        quartzJobDao.update(job);
        getJobDetail(job).getJobDataMap().put(getJobIdentity(job), job);
    }

    /**
     * 设置job的开始schedule时间
     * @param job
     * @throws SchedulerException
     */
    
    @Transactional
    public void updateStartTime(QuartzJobDTO job) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
        //获取trigger，即在spring配置文件中定义的 bean id="myTrigger"
        CronTriggerImpl trigger = (CronTriggerImpl) scheduler.getTrigger(triggerKey);
        trigger.setStartTime(job.getStartTime());
        //按新的trigger重新设置job执行
        scheduler.rescheduleJob(triggerKey, trigger);

        // 更新 job 信息到数据库
        job.setStartTime(trigger.getStartTime());
        job.setNextTime(trigger.getNextFireTime());
        job.setPreviousTime(trigger.getPreviousFireTime());
        quartzJobDao.update(job);
        getJobDetail(job).getJobDataMap().put(getJobIdentity(job), job);
    }

    
    public List<QuartzJobDTO> findByJobStatus(String statusRunning) {
        QuartzJobDTO quartzJobDTO=new QuartzJobDTO();
        quartzJobDTO.setJobStatus(statusRunning);
        return quartzJobDao.findList(quartzJobDTO);
    }

    
    public void updateByIdAndTime(QuartzJobDTO QuartzJobDTO) {

    }


}
