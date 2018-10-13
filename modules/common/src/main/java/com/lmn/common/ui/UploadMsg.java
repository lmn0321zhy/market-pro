package com.lmn.common.ui;

/**
 * Kindeditor 文件上传返回数据格式
 */
public class UploadMsg {
    private String id;
    //上传验证消息
    private String message;
    //文件名称
    private String name;
    //文件大小
    private long size;
    //连接路径
    private String url;
    //meidia 表id
    private String metaId;

    private boolean sucess;
    /**
     * 0 正常
     * 1 Bad link.
     * 2 No link in upload response.
     * 3 Error during file upload.
     * 4 Parsing response failed.
     * 5 File too text-large.
     * 6 Invalid file type.
     * 7 File can be uploaded only to same domain in IE 8 and IE 9.
     * <p>
     * 1: '上传配置错误。',
     * 2: '文件服务器无响应。',
     * 3: '文件上传错误。',
     * 4: '解析响应失败。',
     * 5: '上传文件大小超过限制。',
     * 6: '服务器不支持此文件类型。',
     * 7: '文件只能上传同一域在IE 8和IE 9。'
     */

    private int code;

    public boolean isSucess() {
        return sucess;
    }

    public void setSucess(boolean sucess) {
        this.sucess = sucess;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMetaId() {
        return metaId;
    }

    public void setMetaId(String metaId) {
        this.metaId = metaId;
    }
}
