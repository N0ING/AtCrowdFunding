package com.no.crowd.mvc.handler;

import com.github.pagehelper.PageInfo;
import com.no.crowd.entity.Role;
import com.no.crowd.entity.RoleExample;
import com.no.crowd.service.RoleService;
import com.no.crowd.util.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class RoleHandler {

    @Autowired
    public RoleService roleService;


    @ResponseBody
    @RequestMapping("role/remove/by/role/id/array.json")
    public ResultEntity<String> removeByIdArray(@RequestBody List<Integer> roleIdList){

        roleService.removeRole(roleIdList);

        return  ResultEntity.successWithoutData();
    }


    @ResponseBody
    @RequestMapping("role/update.json")
    public ResultEntity<String> updatePageInfo(Role role){

        roleService.updateRole(role);

        return  ResultEntity.successWithoutData();
    }


    @ResponseBody
    @RequestMapping("role/save.json")
    public ResultEntity<String> savePageInfo(Role role){

        roleService.saveRole(role);

        return ResultEntity.successWithoutData();
    }


    @ResponseBody
    @RequestMapping("role/get/page/info.json")
    public ResultEntity<PageInfo<Role>> getPageInfo(
            @RequestParam(value = "keyword",defaultValue = "") String keyword,
            @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize,
            ModelMap modelMap
    ){
        //调用分页数据
        PageInfo<Role> pageInfo = roleService.getPageInfo(pageNum, pageSize, keyword);


        return ResultEntity.successWithData(pageInfo);
    }

}
