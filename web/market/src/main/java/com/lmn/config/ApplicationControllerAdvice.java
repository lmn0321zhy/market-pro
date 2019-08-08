package com.lmn.config;

import cn.wenwuyun.common.persistence.ApiData;
import cn.wenwuyun.common.service.ServiceException;
import cn.wenwuyun.common.utils.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局控制器
 */
@ControllerAdvice
@ResponseBody
public class ApplicationControllerAdvice {
    private Logger logger = LoggerFactory.getLogger(ApplicationControllerAdvice.class);

    /**
     * 全局错误信息拦截
     */
    @ExceptionHandler({Throwable.class, AuthenticationException.class})
    public ApiData throwable(NativeWebRequest request, Exception e) {
        String msg = "系统运行异常,请联系系统管理员以便解决问题!";
        if (e instanceof AuthenticationException) {
            if (e.getMessage() != null) {
                msg = StringUtils.replace(e.getMessage(), "msg:", "");
            }
        } else if (e instanceof UnauthorizedException) {
            msg = "帐号未经授权访问本资源";
        } else if (e instanceof ServiceException) {
            msg = StringUtils.replace(e.getMessage(), "msg:", "");
        } else {
            e.printStackTrace();
        }
        return new ApiData<>().setMessage(msg);
    }


    /**
     * 404拦截
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ApiData noHandlerFoundException(NoHandlerFoundException e) {
        //logger.error(e.getMessage());
        e.printStackTrace();
        return new ApiData<>().setMessage("请求的路径不存在!");
    }
}
