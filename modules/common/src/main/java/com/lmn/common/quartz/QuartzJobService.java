package com.lmn.common.quartz;

import com.lmn.common.quartz.dto.QuartzJobDTO;
import org.quartz.SchedulerException;

import java.util.List;

/**
 * Created by lmn on 2018-10-10.
 */
public interface QuartzJobService {
    QuartzJobDTO getJob(String jobName, String jobGroup) throws SchedulerException;
    List<QuartzJobDTO> getAllJobs() throws SchedulerException;
    List<QuartzJobDTO> getRunningJob() throws SchedulerException;
    boolean addJob(QuartzJobDTO job) throws SchedulerException;
    boolean pauseJob(QuartzJobDTO job);
    boolean resumeJob(QuartzJobDTO job);
    boolean deleteJob(QuartzJobDTO job);
    void startJob(QuartzJobDTO scheduleJob) throws SchedulerException;
    void updateCronExpression(QuartzJobDTO job) throws SchedulerException;
    void updateStartTime(QuartzJobDTO job) throws SchedulerException;
    List<QuartzJobDTO> findByJobStatus(String statusRunning);
    void updateByIdAndTime(QuartzJobDTO QuartzJobDTO);
}
