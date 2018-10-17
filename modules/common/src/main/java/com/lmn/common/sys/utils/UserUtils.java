package com.lmn.common.sys.utils;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lmn.common.base.BaseEntity;
import com.lmn.common.dao.MenuDao;
import com.lmn.common.dao.OfficeDao;
import com.lmn.common.dao.RoleDao;
import com.lmn.common.dao.UserDao;
import com.lmn.common.security.Principal;
import com.lmn.common.sys.entity.Menu;
import com.lmn.common.sys.entity.Office;
import com.lmn.common.sys.entity.Role;
import com.lmn.common.sys.entity.User;
import com.lmn.common.ui.SimpleTree;
import com.lmn.common.utils.CacheUtils;
import com.lmn.common.utils.SpringContextHolder;
import com.lmn.common.utils.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户工具类
 */
public class UserUtils {

    private static UserDao userDao = SpringContextHolder.getBean(UserDao.class);
    private static RoleDao roleDao = SpringContextHolder.getBean(RoleDao.class);
    private static MenuDao menuDao = SpringContextHolder.getBean(MenuDao.class);
//    private static AreaDao areaDao = SpringContextHolder.getBean(AreaDao.class);
    private static OfficeDao officeDao = SpringContextHolder.getBean(OfficeDao.class);

    public static final String USER_CACHE = "userCache";
    public static final String USER_CACHE_ID_ = "id_";
    public static final String USER_CACHE_LOGIN_NAME_ = "ln";
    public static final String USER_CACHE_LIST_BY_OFFICE_ID_ = "oid_";


    public static final String CACHE_AUTH_INFO = "authInfo";
    public static final String CACHE_ROLE_LIST = "roleList";
    public static final String CACHE_MENU_LIST = "menuList";
    public static final String CACHE_AREA_LIST = "areaList";
    public static final String CACHE_OFFICE_LIST = "officeList";
    public static final String CACHE_OFFICE_ALL_LIST = "officeAllList";
    public static final String CACHE_CATEGORY_LIST = "categoryList";
    public static final String CACHE_CULTURAL_LIST = "culturalList";

    /**
     * 根据ID获取用户
     *
     * @param id
     * @return 取不到返回null
     */
    public static User get(String id) {
        User user = (User) CacheUtils.get(USER_CACHE, USER_CACHE_ID_ + id);
        if (user == null) {
            user = userDao.get(id);
            if (user == null) {
                return null;
            }
            user.setRoleList(roleDao.findList(new Role(user)));
            CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
            CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
        }
        return user;
    }

    /**
     * 根据登录名获取用户
     *
     * @param loginName
     * @return 取不到返回null
     */
    public static User getByLoginName(String loginName) {
        User user = (User) CacheUtils.get(USER_CACHE, USER_CACHE_LOGIN_NAME_ + loginName);
        if (user == null) {
            user = userDao.getByLoginName(new User(null, loginName));
            if (user == null) {
                return null;
            }
            user.setRoleList(roleDao.findList(new Role(user)));
            CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
            CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
        }
        return user;
    }

    /**
     * 清除当前用户缓存
     */
    public static void clearCache() {
        removeCache(CACHE_AUTH_INFO);
        removeCache(CACHE_ROLE_LIST);
        removeCache(CACHE_MENU_LIST);
        removeCache(CACHE_AREA_LIST);
        removeCache(CACHE_OFFICE_LIST);
        removeCache(CACHE_OFFICE_ALL_LIST);
        removeCache(CACHE_CATEGORY_LIST);
        UserUtils.clearCache(getUser());
        //此方法副作用太大,正式环境要取消
        //UserUtils.removeAllCache();
    }

    /**
     * 清除指定用户缓存
     *
     * @param user
     */
    public static void clearCache(User user) {
        CacheUtils.remove(USER_CACHE, USER_CACHE_ID_ + user.getId());
        CacheUtils.remove(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName());
        CacheUtils.remove(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getOldLoginName());
        if (user.getOffice() != null && user.getOffice().getId() != null) {
            CacheUtils.remove(USER_CACHE, USER_CACHE_LIST_BY_OFFICE_ID_ + user.getOffice().getId());
        }
    }

    /**
     * 获取当前用户
     *
     * @return 取不到返回 new User()
     */
    public static User getUser() {
        Principal principal = getPrincipal();
        if (principal != null) {
            User user = get(principal.getId());
            if (user != null) {
                return user;
            }
            return new User();
        }
        // 如果没有登录，则返回实例化空的User对象。
        return new User();
    }

    public static User getUser(String id) {
        return userDao.get(id);
    }

    /**
     * 获取当前用户角色列表
     *
     * @return
     */
    public static List<Role> getRoleList() {
        @SuppressWarnings("unchecked")
        List<Role> roleList = (List<Role>) getCache(CACHE_ROLE_LIST);
        if (roleList == null) {

            User user = getUser();
            if (user.isAdmin() || user.isRootRole()) {
                roleList = roleDao.findAllList(new Role());
            } else {
                Role role = new Role();
                role.getSqlMap().put("dsf", dataScopeFilter(user, "o", "u"));
                roleList = roleDao.findList(role);
            }
            putCache(CACHE_ROLE_LIST, roleList);
        }
        return roleList;
    }

    /**
     * 获取当前用户授权菜单
     *
     * @return
     */
    public static List<Menu> getMenuList() {
        @SuppressWarnings("unchecked")
        List<Menu> menuList = (List<Menu>) getCache(CACHE_MENU_LIST);
        if (menuList == null) {
            User user = getUser();
            if (user.isAdmin() || user.isRootRole()) {
                menuList = menuDao.findAllList(new Menu());
            } else {
                Menu m = new Menu();
                m.setUserId(user.getId());
                menuList = menuDao.findByUserId(m);
            }
            putCache(CACHE_MENU_LIST, menuList);
        }
        return menuList;
    }

    /**
     * 通过parentId获取当前用户的某一级授权菜单
     *
     * @return
     */
    public static List<HashMap<String, Object>> getMenuListByParentId(String parentId) {
        @SuppressWarnings("unchecked")
        List<HashMap<String, Object>> menuList = (List<HashMap<String, Object>>) getCache(CACHE_MENU_LIST + "_" + parentId);
        if (menuList == null && StringUtils.isNotBlank(parentId)) {
            User user = getUser();
            if (user.isAdmin() || user.isRootRole()) {
                Menu menu = new Menu();
                menu.setParent(new Menu(parentId));
                menuList = menuDao.findByParentId(menu);
            } else {
                Menu m = new Menu();
                m.setUserId(user.getId());
                m.setParent(new Menu(parentId));
                menuList = menuDao.findByUserIdAndParentId(m);
            }
            putCache(CACHE_MENU_LIST + "_" + parentId, menuList);
        }
        return menuList;
    }

    /**
     * 判断是否有对应角色,不能在@getMenuList与@getRoleList中使用,
     * 因为系统在认证前需要调用上述两个方法,会形成递归调用
     */
    public static boolean hasRole(String role) {
        return getSubject() != null && getSubject().hasRole(role);
    }


    /**
     * 获取指定id指定层级的所有菜单
     *
     * @return
     */
    public static List<SimpleTree> getMenuListByLevel(String parentId, int level) {
        List<SimpleTree> menuList = Lists.newArrayList();
        if (parentId == null) return menuList;
        List<Menu> menus = UserUtils.getMenuList();
        for (int i = 0, j = menus.size(); i < j; i++) {
            Menu e = menus.get(i);
            if (e.getParent() != null && parentId.equals(e.getParent().getId()) && e.getIsShow().equals("1")) {
                SimpleTree simpleTree = new SimpleTree();
                simpleTree.setId(e.getId());
                simpleTree.setpId(e.getParentId());
                simpleTree.setName(e.getName());
                simpleTree.setIcon(e.getIcon());
                simpleTree.setUrl(e.getHref());
                menuList.add(simpleTree);
                if (level > 1) {
                    menuList.addAll(getMenuListByLevel(e.getId(), level - 1));
                }
            }
        }
        return menuList;
    }

    /**
     * 获取指定id的子菜单
     *
     * @param parentId
     * @return
     */
    public static List<Menu> getMenuList(String parentId) {
        List<Menu> menuList = Lists.newArrayList();
        Menu.sortList(menuList, UserUtils.getMenuList(), parentId, false);
        List<Menu> menus = Lists.newArrayList();
        for (Menu menu : menuList) {
            if ("1".equals(menu.getIsShow())) {
                menus.add(menu);
            }
        }

        return menus;
    }

    /**
     * 获取当前用户有权限访问的部门
     *
     * @return
     */
    public static List<Office> getOfficeList() {
        @SuppressWarnings("unchecked")
        List<Office> officeList = (List<Office>) getCache(CACHE_OFFICE_LIST);
        if (officeList == null) {
            User user = getUser();
            if (user.isAdmin() || user.isRootRole()) {
                officeList = officeDao.findAllList(new Office());
            } else {
                Office office = new Office();
                office.setCurrentUser(user);
                office.getSqlMap().put("dsf", dataScopeFilter(user, "a", ""));
                officeList = officeDao.findList(office);
            }
            putCache(CACHE_OFFICE_LIST, officeList);
        }
        return officeList;
    }

    /**
     * 获取当前用户有权限访问的部门
     *
     * @return
     */
    public static List<Office> getOfficeAllList() {
        @SuppressWarnings("unchecked")
        List<Office> officeList = (List<Office>) getCache(CACHE_OFFICE_ALL_LIST);
        if (officeList == null) {
            officeList = officeDao.findAllList(new Office());
        }
        return officeList;
    }


    /**
     * 数据范围过滤
     *
     * @param officeAlias 机构表别名，多个用“,”逗号隔开。
     * @param userAlias   用户表别名，多个用“,”逗号隔开，传递空，忽略此参数
     * @return 标准连接条件对象
     */
    public static String dataScopeFilter(String officeAlias, String userAlias) {
        return dataScopeFilter(getUser(), officeAlias, userAlias);
    }

    /**
     * 数据范围过滤
     *
     * @param user        当前用户对象，通过“entity.getCurrentUser()”获取
     * @param officeAlias 机构表别名，多个用“,”逗号隔开。
     * @param userAlias   用户表别名，多个用“,”逗号隔开，传递空，忽略此参数
     * @return 标准连接条件对象
     */
    public static String dataScopeFilter(User user, String officeAlias, String userAlias) {

        StringBuilder sqlString = new StringBuilder();

        // 进行权限过滤，多个角色权限范围之间为或者关系。
        List<String> dataScope = Lists.newArrayList();

        // 超级管理员，跳过权限过滤
        if (!user.isAdmin()) {
            boolean isDataScopeAll = false;
            for (Role r : user.getRoleList()) {
                for (String oa : org.apache.commons.lang3.StringUtils.split(officeAlias, ",")) {
                    if (!dataScope.contains(r.getDataScope()) && org.apache.commons.lang3.StringUtils.isNotBlank(oa)) {
                        if (Role.DATA_SCOPE_ALL.equals(r.getDataScope())) {
                            isDataScopeAll = true;
                        } else if (Role.DATA_SCOPE_COMPANY_AND_CHILD.equals(r.getDataScope())) {
                            sqlString.append(" OR " + oa + ".id = '" + user.getCompany().getId() + "'");
                            sqlString.append(" OR " + oa + ".parent_ids LIKE '" + user.getCompany().getParentIds() + user.getCompany().getId() + ",%'");
                        } else if (Role.DATA_SCOPE_COMPANY.equals(r.getDataScope())) {
                            sqlString.append(" OR " + oa + ".id = '" + user.getCompany().getId() + "'");
                            // 包括本公司下的部门 （type=1:公司；type=2：部门）
                            sqlString.append(" OR (" + oa + ".parent_id = '" + user.getCompany().getId() + "' AND " + oa + ".type = '2')");
                        } else if (Role.DATA_SCOPE_OFFICE_AND_CHILD.equals(r.getDataScope())) {
                            sqlString.append(" OR " + oa + ".id = '" + user.getOffice().getId() + "'");
                            sqlString.append(" OR " + oa + ".parent_ids LIKE '" + user.getOffice().getParentIds() + user.getOffice().getId() + ",%'");
                        } else if (Role.DATA_SCOPE_OFFICE.equals(r.getDataScope())) {
                            sqlString.append(" OR " + oa + ".id = '" + user.getOffice().getId() + "'");
                        } else if (Role.DATA_SCOPE_CUSTOM.equals(r.getDataScope())) {
//							String officeIds =  StringUtils.join(r.getOfficeIdList(), "','");
//							if (StringUtils.isNotEmpty(officeIds)){
//								sqlString.append(" OR " + oa + ".id IN ('" + officeIds + "')");
//							}
                            sqlString.append(" OR EXISTS (SELECT 1 FROM sys_role_office WHERE role_id = '" + r.getId() + "'");
                            sqlString.append(" AND office_id = " + oa + ".id)");
                        }
                        //else if (Role.DATA_SCOPE_SELF.equals(r.getDataScope())){
                        dataScope.add(r.getDataScope());
                    }
                }
            }
            // 如果没有全部数据权限，并设置了用户别名，则当前权限为本人；如果未设置别名，当前无权限为已植入权限
            if (!isDataScopeAll) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(userAlias)) {
                    for (String ua : org.apache.commons.lang3.StringUtils.split(userAlias, ",")) {
                        sqlString.append(" OR " + ua + ".id = '" + user.getId() + "'");
                    }
                } else {
                    for (String oa : org.apache.commons.lang3.StringUtils.split(officeAlias, ",")) {
                        //sqlString.append(" OR " + oa + ".id  = " + user.getOffice().getId());
                        sqlString.append(" OR " + oa + ".id IS NULL");
                    }
                }
            } else {
                // 如果包含全部权限，则去掉之前添加的所有条件，并跳出循环。
                sqlString = new StringBuilder();
            }
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(sqlString.toString())) {
            return " AND (" + sqlString.substring(4) + ")";
        }
        return "";
    }

    /**
     * 数据范围过滤（符合业务表字段不同的时候使用，采用exists方法）
     *
     * @param entity       当前过滤的实体类
     * @param sqlMapKey    sqlMap的键值，例如设置“dsf”时，调用方法：${sqlMap.sdf}
     * @param officeWheres office表条件，组成：部门表字段=业务表的部门字段
     * @param userWheres   user表条件，组成：用户表字段=业务表的用户字段
     * @example dataScopeFilter(user, "dsf", "id=a.office_id", "id=a.create_by");
     * dataScopeFilter(entity, "dsf", "code=a.jgdm", "no=a.cjr"); // 适应于业务表关联不同字段时使用，如果关联的不是机构id是code。
     */
    public static void dataScopeFilter(BaseEntity<?> entity, String sqlMapKey, String officeWheres, String userWheres) {

        User user = UserUtils.getUser();

        // 如果是超级管理员，则不过滤数据
        if (user.isAdmin()) {
            return;
        }

        // 数据范围（1：所有数据；2：所在公司及以下数据；3：所在公司数据；4：所在部门及以下数据；5：所在部门数据；8：仅本人数据；9：按明细设置）
        StringBuilder sqlString = new StringBuilder();

        // 获取到最大的数据权限范围
        String roleId = "";
        int dataScopeInteger = 8;
        for (Role r : user.getRoleList()) {
            int ds = Integer.valueOf(r.getDataScope());
            if (ds == 9) {
                roleId = r.getId();
                dataScopeInteger = ds;
                break;
            } else if (ds < dataScopeInteger) {
                roleId = r.getId();
                dataScopeInteger = ds;
            }
        }
        String dataScopeString = String.valueOf(dataScopeInteger);

        // 生成部门权限SQL语句
        for (String where : org.apache.commons.lang3.StringUtils.split(officeWheres, ",")) {
            if (Role.DATA_SCOPE_COMPANY_AND_CHILD.equals(dataScopeString)) {
                // 包括本公司下的部门 （type=1:公司；type=2：部门）
                sqlString.append(" AND EXISTS (SELECT 1 FROM SYS_OFFICE");
                sqlString.append(" WHERE type='2'");
                sqlString.append(" AND (id = '" + user.getCompany().getId() + "'");
                sqlString.append(" OR parent_ids LIKE '" + user.getCompany().getParentIds() + user.getCompany().getId() + ",%')");
                sqlString.append(" AND " + where + ")");
            } else if (Role.DATA_SCOPE_COMPANY.equals(dataScopeString)) {
                sqlString.append(" AND EXISTS (SELECT 1 FROM SYS_OFFICE");
                sqlString.append(" WHERE type='2'");
                sqlString.append(" AND id = '" + user.getCompany().getId() + "'");
                sqlString.append(" AND " + where + ")");
            } else if (Role.DATA_SCOPE_OFFICE_AND_CHILD.equals(dataScopeString)) {
                sqlString.append(" AND EXISTS (SELECT 1 FROM SYS_OFFICE");
                sqlString.append(" WHERE (id = '" + user.getOffice().getId() + "'");
                sqlString.append(" OR parent_ids LIKE '" + user.getOffice().getParentIds() + user.getOffice().getId() + ",%')");
                sqlString.append(" AND " + where + ")");
            } else if (Role.DATA_SCOPE_OFFICE.equals(dataScopeString)) {
                sqlString.append(" AND EXISTS (SELECT 1 FROM SYS_OFFICE");
                sqlString.append(" WHERE id = '" + user.getOffice().getId() + "'");
                sqlString.append(" AND " + where + ")");
            } else if (Role.DATA_SCOPE_CUSTOM.equals(dataScopeString)) {
                sqlString.append(" AND EXISTS (SELECT 1 FROM sys_role_office ro123456, sys_office o123456");
                sqlString.append(" WHERE ro123456.office_id = o123456.id");
                sqlString.append(" AND ro123456.role_id = '" + roleId + "'");
                sqlString.append(" AND o123456." + where + ")");
            }
        }
        // 生成个人权限SQL语句
        for (String where : org.apache.commons.lang3.StringUtils.split(userWheres, ",")) {
            if (Role.DATA_SCOPE_SELF.equals(dataScopeString)) {
                sqlString.append(" AND EXISTS (SELECT 1 FROM sys_user");
                sqlString.append(" WHERE id='" + user.getId() + "'");
                sqlString.append(" AND " + where + ")");
            }
        }

//		System.out.println("dataScopeFilter: " + sqlString.toString());

        // 设置到自定义SQL对象
        entity.getSqlMap().put(sqlMapKey, sqlString.toString());

    }

    /**
     * 获取授权主要对象
     */
    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    /**
     * 获取当前登录者对象
     */
    public static Principal getPrincipal() {
        try {
            Subject subject = SecurityUtils.getSubject();
            Principal principal = (Principal) subject.getPrincipal();
            if (principal != null) {
                return principal;
            }
//			subject.logout();
        } catch (UnavailableSecurityManagerException e) {

        } catch (InvalidSessionException e) {

        }
        return null;
    }

    public static Session getSession() {
        try {
            Subject subject = SecurityUtils.getSubject();
            Session session = subject.getSession(false);
            if (session == null) {
                session = subject.getSession();
            }
            if (session != null) {
                return session;
            }
//			subject.logout();
        } catch (InvalidSessionException e) {

        }
        return null;
    }

    /**
     * 是否是验证码登录
     *
     * @param useruame 用户名
     * @param isFail   计数加1
     * @param clean    计数清零
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean isValidateCodeLogin(String useruame, boolean isFail, boolean clean) {
        Map<String, Integer> loginFailMap = (Map<String, Integer>) CacheUtils.get("loginFailMap");
        if (loginFailMap == null) {
            loginFailMap = Maps.newHashMap();
            CacheUtils.put("loginFailMap", loginFailMap);
        }
        Integer loginFailNum = loginFailMap.get(useruame);
        if (loginFailNum == null) {
            loginFailNum = 0;
        }
        if (isFail) {
            loginFailNum++;
            loginFailMap.put(useruame, loginFailNum);
        }
        if (clean) {
            loginFailMap.remove(useruame);
        }
        return loginFailNum >= 3;
    }

    // ============== User Cache ==============

    public static Object getCache(String key) {
        return getCache(key, null);
    }

    public static Object getCache(String key, Object defaultValue) {
//		Object obj = getCacheMap().get(key);
        Object obj = getSession().getAttribute(key);
        return obj == null ? defaultValue : obj;
    }

    public static void putCache(String key, Object value) {
        if (getSession() != null)
            getSession().setAttribute(key, value);
    }

    public static void removeCache(String key) {
        if (getSession() != null)
            getSession().removeAttribute(key);
    }

    public synchronized static void removeAllCache() {
        if (getSession() == null) return;
        for (Object o : getSession().getAttributeKeys()) {
            getSession().removeAttribute(o);
        }
    }

//	public static Map<String, Object> getCacheMap(){
//		Principal principal = getPrincipal();
//		if(principal!=null){
//			return principal.getCacheMap();
//		}
//		return new HashMap<String, Object>();
//	}

}
