package com.lmn.common.ui;

import java.util.Date;

/**
 * 文件信息
 */
public class FileInfo {
    private String id;
    //是否为目录
    private boolean is_dir;
    //目录下是否存在文件
    private boolean has_file;
    //文件大小
    private long size;
    //文件扩展名
    private String type;
    //文件名
    private String name;
    //文件路径
    private String url;
    //时间
    private Date datetime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getIs_dir() {
        return is_dir;
    }

    public void setIs_dir(boolean is_dir) {
        this.is_dir = is_dir;
    }

    public boolean isHas_file() {
        return has_file;
    }

    public void setHas_file(boolean has_file) {
        this.has_file = has_file;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }
}
