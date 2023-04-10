package com.no.crowd.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.no.crowd.entity.Admin;
import com.no.crowd.entity.Role;
import com.no.crowd.entity.RoleExample;
import com.no.crowd.mapper.RoleMapper;
import com.no.crowd.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    public RoleMapper roleMapper;

    @Override
    public PageInfo<Role> getPageInfo(Integer pageNum, Integer pageSize, String keyword) {

        //调用pageHelper的静态方法开启分页
        PageHelper.startPage(pageNum, pageSize);

        //执行查询
        List<Role> roles = roleMapper.selectRoleByKeyword(keyword);

        //封装到PageInfo中
        return new PageInfo<>(roles);
    }

    @Override
    public void saveRole(Role role) {
        roleMapper.insert(role);
    }

    @Override
    public void updateRole(Role role) {
        roleMapper.updateByPrimaryKey(role);
    }

    @Override
    public void removeRole(List<Integer> roleIdList) {

        RoleExample example = new RoleExample();
        RoleExample.Criteria criteria = example.createCriteria();
        //where id in (5,3,2)
        criteria.andIdIn(roleIdList);

        roleMapper.deleteByExample(example);
    }

    @Override
    public List<Role> getAssignedRole(Integer adminId) {

        return  roleMapper.selectAssignedRole(adminId);
    }

    @Override
    public List<Role> getUnAssignedRole(Integer adminId) {
        return roleMapper.selectUnAssignedRole(adminId);
    }
}
