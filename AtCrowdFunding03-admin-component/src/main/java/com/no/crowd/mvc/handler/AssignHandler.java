package com.no.crowd.mvc.handler;


import com.no.crowd.entity.Auth;
import com.no.crowd.entity.Role;
import com.no.crowd.service.AdminService;
import com.no.crowd.service.AuthService;
import com.no.crowd.service.RoleService;
import com.no.crowd.util.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class AssignHandler {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private AuthService authService;

    @ResponseBody
    @RequestMapping("assign/do/role/assign/auth.json")
    public ResultEntity<String> savaAuthRoleRelation(@RequestBody Map<String,List<Integer>> map ){

        authService.savaAuthRoleRelation(map);

        return  ResultEntity.successWithoutData();
    }


    @ResponseBody
    @RequestMapping("assign/get/assigned/auth/id/by/role/id.json")
    public ResultEntity<List<Integer>> getAssignAuthIdByRoleId(@RequestParam("roleId") Integer roleId){

       List<Integer> authIdList = authService.getAssignAuthIdByRoleId(roleId);

       return  ResultEntity.successWithData(authIdList);

    }


    @ResponseBody
    @RequestMapping("assign/get/all/auth.json")
    public ResultEntity<List<Auth>> getAllAuth(){

        List<Auth> authList = authService.getAll();

        return  ResultEntity.successWithData(authList);
    }

    @RequestMapping("assign/do/role/assign.html")
    public String saveAdminRoleRelationShip(
            @RequestParam("adminId") Integer adminId,
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam("keyword") String keyword,
            //允许用户取消所有管理员的角色
            @RequestParam(value = "roleIdList", required = false) List<Integer> roleIdList
    ){
        adminService.saveAdminRoleRelationShip(adminId,roleIdList);

        return "redirect:/admin/get/page.html?pageNum="+pageNum+"&keyword="+keyword;
    }

    @RequestMapping("assign/to/assign/role/page.html")
    public String toAssignRolePage(@RequestParam("adminId") Integer adminId, ModelMap modelMap){

        // 查已分配的角色
        List<Role> assignedRoleList = roleService.getAssignedRole(adminId);

        // 查未分配的角色
        List<Role> unAssignedRoleList = roleService.getUnAssignedRole(adminId);

        // 存入模型
        modelMap.addAttribute("assignedRoleList",assignedRoleList);
        modelMap.addAttribute("unAssignedRoleList",unAssignedRoleList);

        return  "assign-role";
    }



}
