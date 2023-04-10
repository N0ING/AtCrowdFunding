package com.no.crowd.service;

import com.no.crowd.entity.Auth;

import java.util.List;
import java.util.Map;

public interface AuthService {
    List<Auth> getAll();

    List<Integer> getAssignAuthIdByRoleId(Integer roleId);

    void savaAuthRoleRelation(Map<String, List<Integer>> map);

    List<String> getAssignAuthNameByAdminId(Integer adminId);
}
