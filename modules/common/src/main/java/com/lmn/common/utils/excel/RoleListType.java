package com.lmn.common.utils.excel;


import com.google.common.collect.Lists;
import com.lmn.common.entity.Role;
import com.lmn.common.service.SystemService;
import com.lmn.common.utils.Collections3;
import com.lmn.common.utils.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 字段类型转换
 */
public class RoleListType {

    private static SystemService systemService = SpringContextHolder.getBean(SystemService.class);

    /**
     * 获取对象值（导入）
     */
    public static Object getValue(String val) {
        List<Role> roleList = Lists.newArrayList();
        List<Role> allRoleList = systemService.findAllRole();
        for (String s : StringUtils.split(val, ",")) {
            for (Role e : allRoleList) {
                if (StringUtils.trimToEmpty(s).equals(e.getName())) {
                    roleList.add(e);
                }
            }
        }
        return roleList.size() > 0 ? roleList : null;
    }

    /**
     * 设置对象值（导出）
     */
    public static String setValue(Object val) {
        if (val != null) {
            @SuppressWarnings("unchecked")
            List<Role> roleList = (List<Role>) val;
            return Collections3.extractToString(roleList, "name", ", ");
        }
        return "";
    }

}
