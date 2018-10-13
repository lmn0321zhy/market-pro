package com.lmn.common.quartz;

import com.lmn.common.quartz.dto.QuartzJobDTO;
import com.lmn.common.quartz.service.QuartzJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by lmn on 2018-10-11.
 */
@Component
public class QuartzRunner implements CommandLineRunner {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private QuartzJobService quartzJobService;
    @Override
    public void run(String... strings) throws Exception {
        List<QuartzJobDTO> taskList = quartzJobService.findByJobStatus(QuartzJobDTO.STATUS_RUNNING);
        logger.info("开始初始化加载定时任务......");
        for (QuartzJobDTO job : taskList) {
            try {
                quartzJobService.addJob(job);
            } catch (Exception e) {
                logger.error("add job error: " + job.getJobName() + " " + job.getJobGroup(), e);
            }
        }
        logger.info("完成初始化加载定时任务......");
    }
}
