package com.lmn.common.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.pagehelper.Dialect;
import com.lmn.common.utils.SpringContextHolder;
import com.lmn.common.utils.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 全局配置类
 */
public class Const {
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    public static final String ROOT_ROLE = "root";

    /**
     * 显示/隐藏
     */
    public static final String SHOW = "1";
    public static final String HIDE = "0";

    /**
     * 是/否
     */
    public static final String YES = "1";
    public static final String NO = "0";

    /**
     * 对/错
     */
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    /**
     * 上传文件基础虚拟路径
     */
    public static final String USERFILES_BASE_URL = "/userfiles/";

    private static String baseDir = "";


    private static Environment environment = SpringContextHolder.getBean(Environment.class);


    /**
     * 页面获取常量
     */
    public static Object getConst(String field) {
        try {
            return Const.class.getField(field).get(null);
        } catch (Exception e) {
            // 异常代表无配置，这里什么也不做
        }
        return null;
    }

    //读取配置文件
    @JsonIgnore
    public static String getConfig(String key) {
        return environment.getProperty(key);
    }

    /**
     * 分页大小
     */
    public static int getPageSize() {
        return environment.getProperty("pageSize", Integer.class, 20);
    }

    /**
     * 获取最大文件上传大小
     */
    public static long getMediaSize() {
        return StringUtils.toLong(getConfig("web.maxUploadSize"));
    }


    @JsonIgnore
    public static String getJdbcType() {
        return Dialect.fromJdbcUrl(environment.getProperty("spring.datasource.url"));
    }

    public static String getAppName() {
        return getConfig("app.name");
    }

    public static String getVersion() {
        return getConfig("app.version");
    }

    @JsonIgnore
    public static String getApiPath() {
        return StringUtils.prependIfMissing(getConfig("apiPath"), "/");
    }


    /**
     * 获取前端根路径
     */
    public static String getFrontPath() {
        return StringUtils.prependIfMissing(getConfig("frontPath"), "/");
    }

    /**
     * 获取URL后缀
     */
    public static String getUrlSuffix() {
        return getConfig("urlSuffix");
    }

    /**
     * 用户文件基础路径
     */
    @JsonIgnore
    public static String getUserfilesBaseDir() {
        String path;
        if (StringUtils.isNotBlank(getConfig("userfiles"))) {
            path = getConfig("userfiles");
        } else {
            path = Const.getBaseDir();
        }
        return path;
    }

    /**
     * 用户文件路径
     */
    @JsonIgnore
    public static String getUserfilesDir() {
        return getUserfilesBaseDir() + Const.USERFILES_BASE_URL;
    }

    /**
     * 系统部署路径
     */
    @JsonIgnore
    public static String getBaseDir() {
        if (StringUtils.isBlank(baseDir)) {
            baseDir = System.getProperty("user.dir");
        }
        return baseDir;
    }


    /**
     * 获取工程路径
     *
     * @return
     */
    @JsonIgnore
    public static String getProjectPath() {
        // 如果配置了工程路径，则直接返回，否则自动获取。
        String projectPath = Const.getConfig("projectPath");
        if (StringUtils.isNotBlank(projectPath)) {
            return projectPath;
        }
        try {
            File file = new DefaultResourceLoader().getResource("").getFile();
            if (file != null) {
                while (true) {
                    File f = new File(file.getPath() + File.separator + "src" + File.separator + "main");
                    if (f == null || f.exists()) {
                        break;
                    }
                    if (file.getParentFile() != null) {
                        file = file.getParentFile();
                    } else {
                        break;
                    }
                }
                projectPath = file.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return projectPath;
    }
}
