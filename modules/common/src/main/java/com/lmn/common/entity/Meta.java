package com.lmn.common.entity;


import com.lmn.common.base.DataEntity;
import com.lmn.common.base.IUser;
import com.lmn.common.utils.FileUtils;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * 媒体数据Entity
 */
public class Meta extends DataEntity<Meta> {

    private static final long serialVersionUID = 1L;
    private String name;        // 文件名
    private Long size;        // 文件大小
    private String ext;        // 扩展名
    private String path;        // 文件存放路径
    private String hash;        // 文件hash，考虑用sha1或者用md5

    private MetaContent metaContent;

    private IUser user;

    private List<MetaContent> metaContentList;


    public Meta() {
        super();
    }

    public Meta(String id) {
        super(id);
    }

    @Length(min = 0, max = 255, message = "文件名长度必须介于 0 和 255 之间")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Length(min = 0, max = 10, message = "扩展名长度必须介于 0 和 10 之间")
    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    @Length(min = 0, max = 255, message = "文件存放路径长度必须介于 0 和 255 之间")
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Length(min = 0, max = 64, message = "文件hash，考虑用sha1或者用md5长度必须介于 0 和 64 之间")
    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public List<MetaContent> getMetaContentList() {
        return metaContentList;
    }

    public void setMetaContentList(List<MetaContent> metaContentList) {
        this.metaContentList = metaContentList;
    }

    public MetaContent getMetaContent() {
        return metaContent;
    }

    public void setMetaContent(MetaContent metaContent) {
        this.metaContent = metaContent;
    }


    public IUser getUser() {
        return user;
    }

    public void setUser(IUser user) {
        this.user = user;
    }

    //判断是否为文件
    public boolean getIsImage() {
        return FileUtils.isImage(path);
    }
}