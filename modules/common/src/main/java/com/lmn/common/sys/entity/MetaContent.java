package com.lmn.common.sys.entity;


import com.lmn.common.base.DataEntity;
import org.hibernate.validator.constraints.Length;

/**
 * 媒体关系表Entity
 */
public class MetaContent extends DataEntity<MetaContent> {

    private static final long serialVersionUID = 1L;
    private String metaId;        // 媒体id
    private String contentId;        // 内容id
    private String type;            // 文件类型 对图像传感器：1：原始图2：标记图3：界面截图

    public MetaContent() {
        super();
    }

    public MetaContent(String metaId) {
        this.metaId = metaId;
    }

    public MetaContent(String metaId, String contentId) {
        this.metaId = metaId;
        this.contentId = contentId;
    }

    @Length(min = 1, max = 64, message = "内容id长度必须介于 1 和 64 之间")
    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getMetaId() {
        return metaId;
    }

    public void setMetaId(String metaId) {
        this.metaId = metaId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}