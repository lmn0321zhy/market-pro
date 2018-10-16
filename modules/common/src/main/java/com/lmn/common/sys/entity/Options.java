package com.lmn.common.sys.entity;

import org.hibernate.validator.constraints.Length;

/**
 * 用户配置信息表Entity
 * @author 李顺兴
 * @version 2016-01-14
 */
public class Options extends DataEntity<Options> {
	
	private static final long serialVersionUID = 1L;
	private String title;		// 名称
	private String content;		// 内容,对象按照需求序列化存储
	
	public Options() {
		super();
	}

	public Options(String id){
	    this();
		this.id = id;
	}


	@Length(min=1, max=255, message="名称长度必须介于 1 和 255 之间")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	

}