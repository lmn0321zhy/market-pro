package com.lmn.common.sys.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.lmn.common.base.BaseService;
import com.lmn.common.base.Paging;
import com.lmn.common.dao.MenuDao;
import com.lmn.common.dao.RoleDao;
import com.lmn.common.dao.UserDao;
import com.lmn.common.exception.ServiceException;
import com.lmn.common.mapper.JsonMapper;
import com.lmn.common.security.Digests;
import com.lmn.common.security.SystemAuthorizingRealm;
import com.lmn.common.security.shiro.session.SessionDAO;
import com.lmn.common.servlet.Servlets;
import com.lmn.common.sys.entity.*;
import com.lmn.common.sys.utils.UserUtils;
import com.lmn.common.utils.*;
import com.mchange.v2.log.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统管理，安全相关实体的管理类,包括用户、角色、菜单.
 */
@Component
public class SystemService extends BaseService {

    public static final String HASH_ALGORITHM = "SHA-1";
    public static final int HASH_INTERATIONS = 1024;
    public static final int SALT_SIZE = 8;

    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private MenuDao menuDao;
    @Autowired
    private SessionDAO sessionDao;
    @Autowired
    private SystemAuthorizingRealm systemRealm;
    @Autowired
    private OptionsService optionsService;

    public SessionDAO getSessionDao() {
        return sessionDao;
    }


    //-- User Service --//

    /**
     * 获取用户
     *
     * @param id
     * @return
     */
    public User getUser(String id) {
        return UserUtils.get(id);
    }

    /**
     * 获取用户
     *
     * @param id
     * @return
     */
    public HashMap<String, Object> getUserAndOptions(String id) {
        User user = this.getUser(id);
        Options options = optionsService.get(id);
        if (user == null) {
            return null;
        }
        String optContent = null;
        if (user != null && options != null) {
            optContent = options.getContent();
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("name", user.getName());
        map.put("password", user.getPassword());
        map.put("loginName", user.getLoginName());
        map.put("email", user.getEmail());
        map.put("phone", user.getPhone());
        map.put("options", JsonMapper.fromJsonString(optContent, Map.class));
        return map;
    }

    /**
     * 根据登录名获取用户
     *
     * @param loginName
     * @return
     */
    public User getUserByLoginName(String loginName) {
        return UserUtils.getByLoginName(loginName);
    }

    public PageInfo<User> findUser(HttpServletRequest request, User user) {
        Paging paging = new Paging(request);
        PageHelper.startPage(paging.getPageNum(), paging.getPageSize(), paging.getTotal() == -1);

        if (StringUtils.isNotBlank(paging.getOrderBy())) {
            PageHelper.orderBy(paging.getOrderBy());
        }
        // 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
        user.getSqlMap().put("dsf", UserUtils.dataScopeFilter(UserUtils.getUser(), "o", "a"));

        List<User> list = userDao.findList(user);

        return new PageInfo<>(list);
    }

    /**
     * 无分页查询人员列表
     *
     * @param user
     * @return
     */
    public List<User> findUser(User user) {
        // 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
        user.getSqlMap().put("dsf", UserUtils.dataScopeFilter(UserUtils.getUser(), "o", "a"));
        return userDao.findList(user);
    }

    /**
     * 无分页查询人员列表
     *
     * @param user
     * @return
     */
    public List<User> findExpoAudList(User user) {
        return userDao.findExpoAudList(user);
    }

    /**
     * 通过部门ID获取用户列表，仅返回用户id和name（树查询用户时用）
     *
     * @param officeId
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<User> findUserByOfficeId(String officeId) {
        List<User> list = null; //= (List<User>) CacheUtils.get(UserUtils.USER_CACHE, UserUtils.USER_CACHE_LIST_BY_OFFICE_ID_ + officeId);
        if (list == null) {
            User user = new User();
            user.setOffice(new Office(officeId));
            list = userDao.findUserByOfficeId(user);
            CacheUtils.put(UserUtils.USER_CACHE, UserUtils.USER_CACHE_LIST_BY_OFFICE_ID_ + officeId, list);
        }
        return list;
    }

    public void saveRegiUser(User user) {
        if (StringUtils.isBlank(user.getId())) {
            user.setId(IdGen.uuid());

            //设置角色为访客
            user.setRoleIdList(Lists.newArrayList("6"));

            //添加用户设置为机器用户
            User _u = UserUtils.get("d429664ec2844c6ea9a7dcadf1bccecd");

            user.setUpdateBy(_u);
            user.setCreateBy(_u);
            user.setCreateDate(new Date());
            user.setUpdateDate(user.getCreateDate());
            userDao.insert(user);
            userDao.insertUserRole(user);
        } else {
            // 更新用户数据
            user.preUpdate();
            userDao.update(user);
            if (StringUtils.isNotBlank(user.getId())) {
                // 清除用户缓存
                UserUtils.clearCache(user);
            }
        }
    }

    public void saveUser(User user) {
        if (StringUtils.isBlank(user.getId())) {
            user.preInsert();
            userDao.insert(user);
        } else {
            // 清除原用户机构用户缓存
            User oldUser = userDao.get(user.getId());
            if (oldUser.getOffice() != null && oldUser.getOffice().getId() != null) {
                CacheUtils.remove(UserUtils.USER_CACHE, UserUtils.USER_CACHE_LIST_BY_OFFICE_ID_ + oldUser.getOffice().getId());
            }
            // 更新用户数据
            user.preUpdate();
            userDao.update(user);
        }
        if (StringUtils.isNotBlank(user.getId())) {
            // 更新用户与角色关联
            userDao.deleteUserRole(user);
            if (user.getRoleList() != null && user.getRoleList().size() > 0) {
                userDao.insertUserRole(user);
            } else {
                throw new ServiceException(user.getLoginName() + "没有设置角色！");
            }
            // 清除用户缓存
            UserUtils.clearCache(user);
//			// 清除权限缓存
//			systemRealm.clearAllCachedAuthorizationInfo();
        }
    }

    public void updateUserInfo(User user) {
        user.preUpdate();
        userDao.updateUserInfo(user);
        // 清除用户缓存
        UserUtils.clearCache(user);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
    }

    public void deleteUser(User user) {
        userDao.delete(user);
        // 清除用户缓存
        UserUtils.clearCache(user);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
    }

    public void updatePasswordById(String id, String loginName, String newPassword) {
        User user = new User(id);
        user.setPassword(entryptPassword(newPassword));
        userDao.updatePasswordById(user);
        // 清除用户缓存
        user.setLoginName(loginName);
        UserUtils.clearCache(user);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
    }

    public void updateUserLoginInfo(User user) {
        // 保存上次登录信息
        user.setOldLoginIp(user.getLoginIp());
        user.setOldLoginDate(user.getLoginDate());
        // 更新本次登录信息
        user.setLoginIp(HttpUtils.getRemoteAddr(Servlets.getRequest()));
        user.setLoginDate(new Date());
        userDao.updateLoginInfo(user);
    }

    /**
     * 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
     */
    public static String entryptPassword(String plainPassword) {
        String plain = Encodes.unescapeHtml(plainPassword);
        byte[] salt = Digests.generateSalt(SALT_SIZE);
        byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, HASH_INTERATIONS);
        return Encodes.encodeHex(salt) + Encodes.encodeHex(hashPassword);
    }

    /**
     * 验证密码
     *
     * @param plainPassword 明文密码
     * @param password      密文密码
     * @return 验证成功返回true
     */
    public static boolean validatePassword(String plainPassword, String password) {
        String plain = Encodes.unescapeHtml(plainPassword);
        byte[] salt = Encodes.decodeHex(password.substring(0, 16));
        byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, HASH_INTERATIONS);
        return password.equals(Encodes.encodeHex(salt) + Encodes.encodeHex(hashPassword));
    }


    //-- Role Service --//

    public Role getRole(String id) {
        return roleDao.get(id);
    }

    public Role getRoleByName(String name) {
        Role r = new Role();
        r.setName(name);
        return roleDao.getByName(r);
    }

    public Role getRoleByEnname(String enname) {
        Role r = new Role();
        r.setEnname(enname);
        return roleDao.getByEnname(r);
    }

    public List<Role> findRole(Role role) {
        return roleDao.findList(role);
    }

    public List<Role> findAllRole() {
        return UserUtils.getRoleList();
    }

    public void saveRole(Role role) {
        if (StringUtils.isBlank(role.getId())) {
            role.preInsert();
            roleDao.insert(role);
        } else {
            role.preUpdate();
            roleDao.update(role);
        }
        // 更新角色与菜单关联
        roleDao.deleteRoleMenu(role);
        if (role.getMenuList().size() > 0) {
            roleDao.insertRoleMenu(role);
        }
        // 更新角色与部门关联
        roleDao.deleteRoleOffice(role);
        if (role.getOfficeList().size() > 0) {
            roleDao.insertRoleOffice(role);
        }
        // 清除用户角色缓存
        UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
    }

    public void deleteRole(Role role) {
        roleDao.delete(role);
        // 清除用户角色缓存
        UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
    }

    public Boolean outUserInRole(Role role, User user) {
        List<Role> roles = user.getRoleList();
        for (Role e : roles) {
            if (e.getId().equals(role.getId())) {
                roles.remove(e);
                saveUser(user);
                return true;
            }
        }
        return false;
    }

    public User assignUserToRole(Role role, User user) {
        if (user == null) {
            return null;
        }
        List<String> roleIds = user.getRoleIdList();
        if (roleIds.contains(role.getId())) {
            return null;
        }
        user.getRoleList().add(role);
        saveUser(user);
        return user;
    }

    //-- Menu Service --//

    public Menu getMenu(String id) {
        return menuDao.get(id);
    }

    public List<Menu> findAllMenu() {
        return UserUtils.getMenuList();
    }

    public List<HashMap<String, Object>> findByParentId(String parentId) {
        if (StringUtils.isBlank(parentId)) {
            return null;
        }
        List<HashMap<String, Object>> list = UserUtils.getMenuListByParentId(parentId);
        return list;
    }

    public void saveMenu(Menu menu) {

        // 获取父节点实体
        menu.setParent(this.getMenu(menu.getParent().getId()));

        String newParentIds = menu.getParent().getParentIds() + menu.getParent().getId();
        if (!StringUtils.endsWith(newParentIds, ",")) {
            newParentIds += ",";
        }

        // 设置新的父节点串
        menu.setParentIds(newParentIds);

        // 保存或更新实体
        if (StringUtils.isBlank(menu.getId())) {
            menu.preInsert();
            menuDao.insert(menu);
        } else {
            menu.preUpdate();
            menuDao.update(menu);
        }

        // 更新子节点 parentIds
        Menu m = new Menu();
        m.setParentIds("%," + menu.getId() + ",%");
        List<Menu> list = menuDao.findByParentIdsLike(m);
        for (Menu e : list) {
            if (StringUtils.contains(e.getParentIds(), menu.getId())) {
                e.setParentIds(menu.getParentIds() + menu.getId() + StringUtils.substringAfter(e.getParentIds(), menu.getId()));
            } else {
                e.setParentIds(menu.getParentIds() + menu.getId() + ",");
            }

            menuDao.updateParentIds(e);

            //e.setIsShow(menu.getIsShow());
            menuDao.updateShow(e);
        }
        // 清除用户菜单缓存
        UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
        // 清除日志相关缓存
//        CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
    }

    public void updateMenuSort(Menu menu) {
        menuDao.updateSort(menu);
        // 清除用户菜单缓存
        UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
        // 清除日志相关缓存
//        CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
    }

    public void deleteMenu(Menu menu) {
        menuDao.delete(menu);
        // 清除用户菜单缓存
        UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
        // 清除日志相关缓存
//        CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
    }

    /**
     * 获取Key加载信息
     */
    public static boolean printKeyLoadMessage() {
        return true;
    }


    public List<Role> getRoleListByUserId(String id) {
        return roleDao.findList(new Role(new User(id)));
    }

    public List<User> findUserByName(User user){
        return userDao.findUserByName(user);
    }

}

