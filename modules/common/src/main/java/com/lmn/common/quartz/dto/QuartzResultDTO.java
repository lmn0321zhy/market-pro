package com.lmn.common.quartz.dto;

import com.lmn.common.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * quartz运行结果
 */
@Data
public class QuartzResultDTO  extends BaseEntity{
	private String jobName;
	private String jobGroup;
	private String success;
	private String errorMsg;
	private String duration;
	private Date createTime;
}
