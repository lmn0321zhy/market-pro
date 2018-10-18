package com.lmn.common.shiro;

import com.lmn.common.base.CrudService;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmn on 2018-10-18.
 */
public class RolePermissionService {
    public List<String> findPermissionByRole(@Length(min = 1, max = 100) String name) {
        return new ArrayList<>();
    }
}
