package com.lmn.common.security;

/**
 * 用户和密码（包含验证码）令牌类
 */
public class UsernamePasswordToken extends org.apache.shiro.authc.UsernamePasswordToken {

    private static final long serialVersionUID = 1L;

    private String captcha;

    private String platform;

    public UsernamePasswordToken() {
        super();
    }

    public UsernamePasswordToken(String username, String password,
                                 boolean rememberMe, String host, String captcha) {
        super(username, password, rememberMe, host);
        this.captcha = captcha;
    }

    public UsernamePasswordToken(String username, String password,
                                 boolean rememberMe, String host, String captcha, String platform) {
        super(username, password, rememberMe, host);
        this.captcha = captcha;
        this.platform = platform;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}