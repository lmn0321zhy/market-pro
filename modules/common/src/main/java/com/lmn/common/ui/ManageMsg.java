package com.lmn.common.ui;

import java.util.List;

/**
 * Kindeditor 文件上传返回数据格式
 */
public class ManageMsg {
    //上一级目录
    private String moveup_dir_path;
    //当前目录
    private String current_dir_path;
    //当前路径
    private String current_url;
    //文件数量
    private Integer total_count;
    //文件列表
    private List<FileInfo> file_list;

    public String getMoveup_dir_path() {
        return moveup_dir_path;
    }

    public void setMoveup_dir_path(String moveup_dir_path) {
        this.moveup_dir_path = moveup_dir_path;
    }

    public String getCurrent_dir_path() {
        return current_dir_path;
    }

    public void setCurrent_dir_path(String current_dir_path) {
        this.current_dir_path = current_dir_path;
    }

    public String getCurrent_url() {
        return current_url;
    }

    public void setCurrent_url(String current_url) {
        this.current_url = current_url;
    }

    public Integer getTotal_count() {
        return total_count;
    }

    public void setTotal_count(Integer total_count) {
        this.total_count = total_count;
    }

    public List<FileInfo> getFile_list() {
        return file_list;
    }

    public void setFile_list(List<FileInfo> file_list) {
        this.file_list = file_list;
    }
}
