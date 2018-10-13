package com.lmn.common.security;

import com.lmn.common.base.IUser;

import java.io.Serializable;

/**
 * 授权用户信息
 */
public class Principal implements Serializable, IUser {

    private static final long serialVersionUID = 1L;

    private String id; // 编号
    private String loginName; // 登录名
    private String name; // 姓名


    //		private Map<String, Object> cacheMap;

    public Principal(IUser user) {
        this.id = user.getId();
        this.loginName = user.getLoginName();
        this.name = user.getName();
    }

    public String getId() {
        return id;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getName() {
        return name;
    }

//		@JsonIgnore
//		public Map<String, Object> getCacheMap() {
//			if (cacheMap==null){
//				cacheMap = new HashMap<String, Object>();
//			}
//			return cacheMap;
//		}

    /**
     * 获取SESSIONID
     */
    /*public String getSessionid() {
        try {
            return (String) UserUtils.getSession().getId();
        } catch (Exception e) {
            return "";
        }
    }*/

    @Override
    public String toString() {
        return id;
    }

}