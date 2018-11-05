package com.lmn.common.shiro;

import com.lmn.common.config.Const;
import com.lmn.common.entity.User;
import com.lmn.common.service.UserService;
import com.lmn.common.utils.Encodes;
import com.lmn.common.utils.ShiroUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;

/**
 * 系统安全认证实现类
 */
public class SystemAuthorizingRealm extends AuthorizingRealm {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserService userService;


    public SystemAuthorizingRealm() {
        this.setCachingEnabled(false);
    }

    /**
     * 认证回调函数, 登录时调用
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) {
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;

        // 校验用户名密码
        User user = userService.findByName(token.getUsername());
        if (user != null) {
            if (Const.NO.equals(user.getLoginFlag())) {
                throw new AuthenticationException("msg:该已帐号禁止登录.");
            }
            byte[] salt = Encodes.decodeHex(user.getPassword().substring(0, 16));
            return new SimpleAuthenticationInfo(user,
                    user.getPassword().substring(16), ByteSource.Util.bytes(salt), getName());
        } else {
            return null;
        }
    }

    /**
     * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        User principal = (User) getAvailablePrincipal(principals);
        User user = userService.findByName(principal.getLoginName());
        if (user != null) {
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
//            info.addStringPermission(user.getRole());
            // 添加用户权限
//            info.addStringPermission("user");
            // 添加用户角色信息
            info.addRole(user.getRole());
            return info;
        } else {
            return null;
        }
    }

    @Override
    protected void checkPermission(Permission permission, AuthorizationInfo info) {
        authorizationValidate(permission);
        super.checkPermission(permission, info);
    }

    @Override
    protected boolean[] isPermitted(List<Permission> permissions, AuthorizationInfo info) {
        if (permissions != null && !permissions.isEmpty()) {
            for (Permission permission : permissions) {
                authorizationValidate(permission);
            }
        }
        return super.isPermitted(permissions, info);
    }

    @Override
    public boolean isPermitted(PrincipalCollection principals, Permission permission) {
        //root角色有权限查看所有的数据
        if (hasRole(principals, Const.ROOT_ROLE)) {
            return true;
        }
        authorizationValidate(permission);
        return super.isPermitted(principals, permission);
    }

    @Override
    protected boolean isPermittedAll(Collection<Permission> permissions, AuthorizationInfo info) {
        if (permissions != null && !permissions.isEmpty()) {
            for (Permission permission : permissions) {
                authorizationValidate(permission);
            }
        }
        return super.isPermittedAll(permissions, info);
    }



    /**
     * 设定密码校验的Hash算法与迭代次数
     */
    @PostConstruct
    public void initCredentialsMatcher() {
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(ShiroUtils.HASH_ALGORITHM);
        matcher.setHashIterations(ShiroUtils.HASH_INTERATIONS);
        setCredentialsMatcher(matcher);
    }

    /**
     * 授权验证方法
     *
     * @param permission
     */
    private void authorizationValidate(Permission permission) {
        // 模块授权预留接口
    }
}
