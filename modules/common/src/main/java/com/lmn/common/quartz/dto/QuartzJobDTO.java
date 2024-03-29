package com.lmn.common.quartz.dto;


import com.lmn.common.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * Created by lmn on 2018-10-09.
 */
@Data
public class QuartzJobDTO extends BaseEntity{
    private static final long serialVersionUID = 607415834012939242L;

    public static final String STATUS_RUNNING = "1";
    public static final String STATUS_NOT_RUNNING = "0";
    public static final String STATUS_DELETED = "2";
    public static final String ASYNC = "1"; // job并发运行
    public static final String UNASYNC = "0"; // // job不并发运行

    /**
     * 任务id
     */
    private String id;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务分组，任务名称+组名称应该是唯一的
     */
    private String jobGroup;

    /**
     * 任务初始状态 0禁用 1启用 2删除
     */
    private String jobStatus;

    /**
     * 任务是否有状态（并发与否）
     */
    private String async = "1";

    /**
     * 任务运行时间表达式
     */
    private String cronExpression;

    /**
     * 任务描述
     */
    private String description;


    /**
     * 任务调用实例名
     */
    private String jobBean;

    /**
     * 启动时间
     */
    private Date startTime;

    /**
     * 前一次运行时间
     */
    private Date previousTime;

    /**
     * 下次运行时间
     */
    private Date nextTime;

    public QuartzJobDTO() {
        super();
    }

    public QuartzJobDTO(String jobName, String jobGroup) {
        super();
        this.jobName = jobName;
        this.jobGroup = jobGroup;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + jobName.hashCode();
        hash = 31 * hash + jobGroup.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || (obj.getClass() != this.getClass())) {
            return false;
        }
        QuartzJobDTO oBean = (QuartzJobDTO) obj;
        if (this.jobName.equals(oBean.jobName) && this.jobGroup.equals(oBean.jobGroup)) {
            return true;
        }
        return false;
    }

}
