package com.lmn.common.entity;


import com.lmn.common.base.BaseEntity;
import com.lmn.common.config.Const;
import lombok.Data;
import java.util.Date;

/**
 * 用户Entity
 */
@Data
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;
    private String companyId;    // 归属公司
    private String officeId;    // 归属部门
    private String loginName;// 登录名
    private String password;// 密码
    private String role;    // 根据角色查询用户条件
    private String no;        // 工号
    private String name;    // 姓名
    private String email;    // 邮箱
    private String phone;    // 电话
    private String mobile;    // 手机
    private String userType;// 用户类型
    private String loginIp;    // 最后登陆IP
    private Date loginDate;    // 最后登陆日期
    private String loginFlag;    // 是否允许登陆
    private String photo;    // 头像

    private String oldLoginName;// 原登录名
    private String newPassword;    // 新密码

    private String oldLoginIp;    // 上次登陆IP
    private Date oldLoginDate;    // 上次登陆日期



    public User() {
        super();
        this.loginFlag = Const.YES;
    }

    public User(String id) {
        super();
        this.id=id;
    }

    public User(String id, String loginName) {
        super();
        this.id=id;
        this.loginName = loginName;
    }
}