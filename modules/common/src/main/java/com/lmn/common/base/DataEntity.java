package com.lmn.common.base;

/**
 * Created by lmn on 2018-10-10.
 */

import com.google.common.collect.Lists;
import com.lmn.common.entity.MetaContent;
import com.lmn.common.entity.User;
import com.lmn.common.utils.MetaUtils;
import com.lmn.common.utils.UserUtils;
import com.lmn.common.utils.IdGen;
import com.lmn.common.utils.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.HashSet;
import java.util.List;


/**
 * 数据Entity类
 */
public abstract class DataEntity<T> extends BaseEntity<T> {

    private static final long serialVersionUID = 1L;

    protected String remarks;    // 备注
    protected User createBy;    // 创建者
    protected Date createDate;    // 创建日期
    protected User updateBy;    // 更新者
    protected Date updateDate;    // 更新日期
    protected String delFlag;    // 删除标记（0：正常；1：删除；2：审核）

    //媒体文件id
    protected String metaIds;

    public DataEntity() {
        super();
        this.delFlag = DEL_FLAG_NORMAL;
    }

    public DataEntity(String id) {
        super(id);
    }

    /**
     * 插入之前执行方法，需要手动调用
     */
    @Override
    public void preInsert() {
        // 不限制ID为UUID，调用setIsNewRecord()使用自定义ID
        if (!this.isNewRecord) {
            setId(IdGen.uuid());
        }
        User user = UserUtils.getUser();
        if (StringUtils.isNotBlank(user.getId())) {
            this.updateBy = user;
            this.createBy = user;
        }
        this.updateDate = new Date();
        this.createDate = this.updateDate;
        saveMeta();
    }

    /**
     * 更新之前执行方法，需要手动调用
     */
    @Override
    public void preUpdate() {
        User user = UserUtils.getUser();
        if (StringUtils.isNotBlank(user.getId())) {
            this.updateBy = user;
        }
        this.updateDate = new Date();
        saveMeta();
    }

    /**
     * 保存媒体关联文件
     */
    private void saveMeta() {
//        //存储媒体数据
        if (StringUtils.isNotBlank(this.getMetaIds())) {
            List<MetaContent> metaContents = Lists.newArrayList();
            String[] metaIds = StringUtils.split(this.getMetaIds(), ",");
            //去重,防止重复id入库
            HashSet<String> hashSet = new HashSet(Lists.newArrayList(metaIds));
            for (String s : Lists.newArrayList(hashSet)) {
                metaContents.add(new MetaContent(s, this.id));
            }
            MetaUtils.saveMetaContent(metaContents);
        }
    }

    @Length(min = 0, max = 255)
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

//    @JsonIgnore
    public User getCreateBy() {
        return createBy;
    }

    public void setCreateBy(User createBy) {
        this.createBy = createBy;
    }

    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

//    @JsonIgnore
    public User getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(User updateBy) {
        this.updateBy = updateBy;
    }

    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Length(min = 1, max = 1)
    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getMetaIds() {
        return metaIds;
    }

    public void setMetaIds(String metaIds) {
        this.metaIds = metaIds;
    }
}
