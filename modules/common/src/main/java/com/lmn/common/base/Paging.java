package com.lmn.common.base;

import com.lmn.common.config.Const;
import org.apache.commons.lang3.math.NumberUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 分页
 */
public class Paging {
    private HttpServletRequest request;
    /**
     * 当前分页
     */
    private int pageNum;
    /**
     * 分页大小
     */
    private int pageSize;
    /**
     * 总记录数
     */
    private long total;
    /**
     * 排序字段
     */
    private String orderBy;


    /**
     * 默认查询全部数据
     */
    public Paging() {
        this.pageNum = 1;
        this.pageSize = 0;
    }


    public Paging(int pageSize) {
        this.pageNum = 1;
        this.pageSize = pageSize;
    }

    public Paging(HttpServletRequest request) {
        this.request = request;

        pageNum = NumberUtils.toInt(this.request.getParameter("pageNum"), 1);
        pageSize = NumberUtils.toInt(this.request.getParameter("pageSize"), Const.getPageSize());
        total = NumberUtils.toInt(this.request.getParameter("total"), -1);
        orderBy = this.request.getParameter("orderBy");
    }

    public Paging(int pageNum, int pageSize, long total) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
    }

    public Paging(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
