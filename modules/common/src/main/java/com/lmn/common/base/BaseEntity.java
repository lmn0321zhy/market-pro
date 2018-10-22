package com.lmn.common.base;

/**
 * Created by lmn on 2018-10-10.
 */


import lombok.Data;

import java.util.Date;
import java.util.UUID;


/**
 * 数据Entity类
 */
@Data
public abstract class BaseEntity<T> {
    private static final long serialVersionUID = 1L;
    protected String id;
    protected String remarks;    // 备注
    protected String createBy;    // 创建者
    protected Date createDate;    // 创建日期
    protected String updateBy;    // 更新者
    protected Date updateDate;    // 更新日期
    protected String delFlag;    // 删除标记（0：正常；1：删除；2：审核）

    /**
     * 生成Id
     * @return
     */
    public String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 插入之前执行方法，需要手动调用
     */
    public void preInsert() {
        setId(uuid());
    }

    /**
     * 更新之前执行方法，需要手动调用
     */
    public void preUpdate() {

    }

}
