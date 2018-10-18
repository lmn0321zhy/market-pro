package com.lmn.common.shiro;

import com.lmn.common.config.Const;
import com.lmn.common.entity.Role;
import com.lmn.common.entity.User;
import com.lmn.common.security.Principal;
import com.lmn.common.security.UsernamePasswordToken;
import com.lmn.common.utils.Encodes;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmn on 2018-10-18.
 */
public class ShiroRealm extends AuthorizingRealm {
    /**
     * 日志对象
     */
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserService userService;
    @Autowired
    private RolePermissionService rolePermissionService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //获取当前登录输入的用户名，等价于(String) principalCollection.fromRealm(getName()).iterator().next();
        String loginName = (String) super.getAvailablePrincipal(principals);
//        User user = (User) principals.getPrimaryPrincipal();
        User user = userService.findByName(loginName);
        if (user != null) {
            //权限信息对象info,用来存放查出的用户的所有的角色（role）及权限（permission）
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            //用户的角色集合
            info.addRole(user.getRole().getName());
            //用户的权限集合
            List<String> permissionByRole = rolePermissionService.findPermissionByRole(user.getRole().getName());
            info.addStringPermissions(permissionByRole);
            return info;
        }
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        logger.info("验证当前Subject时获取到token为：" + token.toString());
        User user = userService.findByName(token.getUsername());
        // 若存在，将此用户存放到登录认证info中，无需自己做密码对比，Shiro会为我们进行密码对比校验
        if (user != null) {
            if (Const.NO.equals(user.getLoginFlag())) {
                throw new AuthenticationException("msg:该已帐号禁止登录.");
            }
            byte[] salt = Encodes.decodeHex(user.getPassword().substring(0, 16));
            return new SimpleAuthenticationInfo(new Principal(user),
                    user.getPassword().substring(16), ByteSource.Util.bytes(salt), getName());
        }
        return null;
    }
}
