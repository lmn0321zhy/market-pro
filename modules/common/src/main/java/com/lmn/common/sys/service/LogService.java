package com.lmn.common.sys.service;

import cn.wenwuyun.common.service.CrudService;
import cn.wenwuyun.modules.sys.dao.LogDao;
import cn.wenwuyun.modules.sys.entity.Log;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 日志Service
 */
@Component
public class LogService extends CrudService<LogDao, Log> {

    public PageInfo<Log> findPage(HttpServletRequest request, Log log) {
        return super.findPage(request, log);
    }

}
