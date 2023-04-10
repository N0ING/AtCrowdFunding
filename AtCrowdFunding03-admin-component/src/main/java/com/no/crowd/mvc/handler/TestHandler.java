package com.no.crowd.mvc.handler;

import com.no.crowd.entity.Admin;
import com.no.crowd.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author NO
 * @create 2022-06-09-9:55
 */

@Controller
public class TestHandler {

    @Autowired
    private AdminService adminService;

    private  Logger logger = LoggerFactory.getLogger(TestHandler.class);

    @ResponseBody
    @RequestMapping("/send/array/three.html")
    public String testArrayThree(@RequestBody List<Integer> array){

        for (Integer number: array) {
           logger.info("number"+number);
        }
        return "success";
    }

    @ResponseBody
    @RequestMapping("/send/array/two.html")
    public String testArrayTwo(@RequestParam("array") List<Integer> array){
        for (Integer number: array) {
            System.out.println(number);
        }
        return "success";
    }




    @ResponseBody
    @RequestMapping("/send/array/one.html")
    public String testArrayOne(@RequestParam("array[]") List<Integer> array){
        for (Integer number: array) {
            System.out.println(number);
        }

        return "success";
    }


    @RequestMapping("/test/ssm.html")
    public String testSSM(ModelMap modelMap){
        List<Admin> adminList = adminService.getAll();

        modelMap.addAttribute("adminList", adminList);
        return  "target";
    }



}
