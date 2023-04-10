package com.no.crowd.service;

import com.github.pagehelper.PageInfo;
import com.no.crowd.entity.Admin;

import java.util.List;

/**
 * @author NO
 * @create 2022-06-08-15:44
 */
public interface AdminService {



    void saveAdmin(Admin admin);

    List<Admin> getAll();

    Admin getAdminByLoginAcct(String loginAcct, String userPswd);

    Admin getAdminByLoginAcct(String username);

    PageInfo<Admin> getPageInfo(String keyword,Integer pageNum,Integer pageSize);

    void remove(Integer adminId);

    Admin getAdminById(Integer adminId);

    void update(Admin admin);

    void saveAdminRoleRelationShip(Integer adminId, List<Integer> roleIdList);

    void removeAdmin(List<Integer> adminIdList);
}
