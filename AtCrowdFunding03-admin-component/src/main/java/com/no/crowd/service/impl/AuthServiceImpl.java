package com.no.crowd.service.impl;

import com.no.crowd.entity.Auth;
import com.no.crowd.entity.AuthExample;
import com.no.crowd.mapper.AuthMapper;
import com.no.crowd.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthMapper authMapper;


    @Override
    public List<Auth> getAll() {
        return authMapper.selectByExample(new AuthExample());
    }

    @Override
    public List<Integer> getAssignAuthIdByRoleId(Integer roleId) {

        return authMapper.selectAssignAuthIdByRoleId(roleId);
    }

    @Override
    public void savaAuthRoleRelation(Map<String, List<Integer>> map) {

        // 获取 roleId 的值
        List<Integer> roleIdList = map.get("roleId");
        Integer roleId = roleIdList.get(0);

        // 删除旧的关联关系
        authMapper.deleteOldRelation(roleId);

        // 3.获取 authIdList
        List<Integer> authIdList = map.get("authIdArray");

        // 4.判断 authIdList 是否有效
        if (authIdList != null && authIdList.size() > 0) {
            authMapper.insertNewRelationship(roleId, authIdList);
        }
    }

    @Override
    public List<String> getAssignAuthNameByAdminId(Integer adminId) {

        return authMapper.selectAssignAuthNameByAdminId(adminId);
    }
}
