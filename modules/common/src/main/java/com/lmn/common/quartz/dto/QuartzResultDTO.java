package com.lmn.common.quartz.dto;

import com.lmn.common.persistence.DataEntity;
import lombok.Data;

import java.util.Date;

/**
 * quartz运行结果
 */
@Data
public class QuartzResultDTO  extends DataEntity<QuartzResultDTO> {

	private String resultId;
	
	private String jobId;
	private String jobName;
	private String jobGroup;
	private boolean success;

	private String errorMsg;
	
	private String duration;
	
	private Date createTime;


}
