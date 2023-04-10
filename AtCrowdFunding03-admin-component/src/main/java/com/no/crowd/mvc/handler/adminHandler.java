package com.no.crowd.mvc.handler;

import com.github.pagehelper.PageInfo;
import com.no.crowd.constant.CrowdConstant;
import com.no.crowd.entity.Admin;
import com.no.crowd.service.AdminService;
import com.no.crowd.util.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author NO
 * @create 2022-06-12-9:47
 */
@Controller
public class adminHandler {

    @Autowired
    private AdminService adminService;

    @RequestMapping("admin/update.html")
    public String update(Admin admin,
                         @RequestParam("pageNum") Integer pageNum,
                         @RequestParam("keyword") String keyword
    ) {

        adminService.update(admin);

        return "redirect:/admin/get/page.html?pageNum=" + pageNum + "&keyword=" + keyword;
    }


    @RequestMapping("admin/to/edit/page.html")
    public String toEditPage(@RequestParam("adminId") Integer adminId,
                             ModelMap modelMap
    ) {
        Admin admin = adminService.getAdminById(adminId);

        modelMap.addAttribute("admin", admin);

        return "admin-edit";
    }

    @RequestMapping("admin/save.html")
    public String save(Admin admin) {

        adminService.saveAdmin(admin);


        //页面跳转：回到分页页面
        return "redirect:/admin/get/page.html?pageNum=" + Integer.MAX_VALUE;

    }


    @ResponseBody
    @RequestMapping("admin/remove/by/admin/id/array.json")
    public ResultEntity<String> removeAdminByIdArray(@RequestBody List<Integer> adminIdList){

        adminService.removeAdmin(adminIdList);

        return  ResultEntity.successWithoutData();
    }


    @RequestMapping("admin/remove/{adminId}/{pageNum}/{keyword}.html")
    public String remove(@PathVariable("adminId") Integer adminId,
                         @PathVariable("pageNum") Integer pageNum,
                         @PathVariable("keyword") String keyword
    ) {

        //执行删除
        adminService.remove(adminId);

        //页面跳转：回到分页页面
        return "redirect:/admin/get/page.html?pageNum=" + pageNum + "&keyword=" + keyword;
    }


    @RequestMapping("admin/get/page.html")
    public String getPageInfo(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            ModelMap modelMap
    ) {
        //调用service方法获取pageInfo
        PageInfo<Admin> pageInfo = adminService.getPageInfo(keyword, pageNum, pageSize);

        //将pageInfo传入模型
        modelMap.addAttribute(CrowdConstant.ATTR_NAME_PAGE_INFO, pageInfo);

        return "admin-page";
    }


    @RequestMapping("admin/do/logout.html")
    public String doLogout(HttpSession session) {

        //强制Session失效
        session.invalidate();

        return "redirect:/admin/to/login/page.html";
    }

    @RequestMapping("admin/do/login.html")
    public String doLogin(@RequestParam("loginAcct") String loginAcct,
                          @RequestParam("userPswd") String userPswd,
                          HttpSession session
    ) {

        // 调用adminService进行校验
        Admin admin = adminService.getAdminByLoginAcct(loginAcct, userPswd);

        //将登录成功的admin对象传入Session
        session.setAttribute(CrowdConstant.ATTR_NAME_LOGIN_ADMIN, admin);

        return "redirect:/admin/to/main/page.html";
    }



}
