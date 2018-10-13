package com.lmn.common.quartz.dto;

import com.lmn.common.persistence.DataEntity;
import lombok.Data;

import java.util.Date;

/**
 * Created by lmn on 2018-10-09.
 */
@Data
public class QuartzJobDTO extends DataEntity<QuartzJobDTO> {
    private static final long serialVersionUID = 607415834012939242L;

    public static final String STATUS_RUNNING = "1";
    public static final String STATUS_NOT_RUNNING = "0";
    public static final String STATUS_DELETED = "2";
    public static final String CONCURRENT_IS = "1";
    public static final String CONCURRENT_NOT = "0";

    /**
     * 任务id
     */
    private String jobId;

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
    private String isConcurrent = "1";

    /**
     * 任务运行时间表达式
     */
    private String cronExpression;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 任务调用类在spring中注册的bean id，如果spingId不为空，则按springId查找
     */
//    private String springId;

    /**
     * 任务调用类名，包名+类名，通过类反射调用 ，如果spingId为空，则按jobClass查找
     */
    private String jobClass;

    /**
     * 任务调用的方法名
     */
//    private String methodName;

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
