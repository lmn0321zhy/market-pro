package com.lmn.common.sys.web;

import cn.wenwuyun.common.persistence.ApiData;
import cn.wenwuyun.common.web.BaseController;
import cn.wenwuyun.modules.sys.entity.Log;
import cn.wenwuyun.modules.sys.service.LogService;
import com.github.pagehelper.PageInfo;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 日志Controller
 */
@Controller
@RequestMapping(value = "${apiPath}/sys/log")
public class LogController extends BaseController {

    @Autowired
    private LogService logService;

    @ResponseBody
    @RequestMapping(value = {"list", ""})
    public ApiData list(HttpServletRequest request, HttpServletResponse response, Log log) {
        ApiData<PageInfo<Log>> apiData = new ApiData<>();
        DateTime dateTime = DateTime.now();
        if (log.getBeginDate() == null) {
            log.setBeginDate(dateTime.withTimeAtStartOfDay().withDayOfMonth(1).toDate());
        }
        if (log.getEndDate() == null) {
            log.setEndDate(dateTime.toDate());
        }
        PageInfo<Log> page = logService.findPage(request, log);
        apiData.setData(page);
        return apiData;
    }
}
