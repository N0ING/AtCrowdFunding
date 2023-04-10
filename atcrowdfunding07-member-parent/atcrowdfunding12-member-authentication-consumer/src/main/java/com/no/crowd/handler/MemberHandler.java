package com.no.crowd.handler;


import com.no.crowd.api.MySQLRemoteService;
import com.no.crowd.api.RedisRemoteService;
import com.no.crowd.config.shortMessageProperties;
import com.no.crowd.constant.CrowdConstant;
import com.no.crowd.entity.po.MemberPO;
import com.no.crowd.entity.vo.MemberLoginVO;
import com.no.crowd.util.CrowdUtil;
import com.no.crowd.util.ResultEntity;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import javax.xml.transform.Result;
import java.util.concurrent.TimeUnit;

@Controller
public class MemberHandler {


    @Autowired
    private com.no.crowd.config.shortMessageProperties shortMessageProperties;

    @Autowired
    private RedisRemoteService redisRemoteService;

    @Autowired
    private MySQLRemoteService mySQLRemoteService;

    @RequestMapping("/admin/to/login/page")
    public String AdminLogin(){
        return "redirect:/admin/do/login.html";
    }


    //会员退出操作
    @RequestMapping("/auth/member/logout")
    public String Logout(HttpSession session){

        session.invalidate();

        return "redirect:/";
    }



    // 登录操作
    @RequestMapping("/auth/member/do/login")
    public String doMemberLogin(
            @RequestParam(value = "loginAcct" ,required = false) String loginAcct,
            @RequestParam(value = "loginPswd" , required = false) String loginPswd,
            ModelMap modelMap,
            HttpSession session) {

        // 远程方法调用，通过loinAcct，得到数据库中的对应Member
        ResultEntity<MemberPO> resultEntity = mySQLRemoteService.getMemberPOByLoginAcctRemote(loginAcct);

        // 判断-查询操作是否成功
        if (ResultEntity.FAILED.equals(resultEntity.getResult())){
            // 查询失败，返回登陆页面
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, resultEntity.getMessage());
            return "member-login";
        }

        // 查询操作成功，则取出MemberPO对象
        MemberPO memberPO = resultEntity.getData();

        // 判断得到的MemberPO是否为空
        if (memberPO == null){
            // 为空则返回登陆页面
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_LOGIN_FAILED);
            return "member-login";
        }

        // 返回的MemberPO非空，取出数据库中的密码（已经加密的）
        String userPswd = memberPO.getUserpswd();

        // 使用BCryptPasswordEncoder，比对表单的密码与数据库中的密码是否匹配
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean matches = passwordEncoder.matches(loginPswd, userPswd);

        // 判断-密码不同
        if (!matches){
            // 返回登陆页面，存入相应的提示信息
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_LOGIN_FAILED);
            return "member-login";
        }

        // 密码匹配，则通过一个LoginMemberVO对象，存入需要在session域通信的用户信息（这样只在session域放一些相对不私秘的信息，保护用户隐私）
        MemberLoginVO loginMember = new MemberLoginVO(memberPO.getId(), memberPO.getUsername(), memberPO.getEmail());

        // 将LoginMemberVO对象存入session域
        session.setAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER,loginMember);

        // 重定向到登陆成功后的主页面
        return "redirect:/auth/member/to/center/page";
    }


    /**
     * 发送手机短信验证码
     * @param phoneNum
     * @return
     */
    @ResponseBody
    @RequestMapping("/auth/member/send/short/message.json")
    public ResultEntity<String> sendMessage(@RequestParam("phoneNum") String phoneNum){

        // 发送验证码到手机号码
        ResultEntity<String> sendMessageResultEntity = CrowdUtil.sendCodeByShortMessage(
                shortMessageProperties.getHost(),
                shortMessageProperties.getPath(),
                shortMessageProperties.getMethod(), phoneNum,
                shortMessageProperties.getAppCode(),
                shortMessageProperties.getSign(),
                shortMessageProperties.getSkin()
        );

        // 判断短信发送结果
        if(ResultEntity.SUCCESS.equals(sendMessageResultEntity.getResult())){

            // 如果发送成功，将短信存入redis
            String code = sendMessageResultEntity.getData();

            String key = CrowdConstant.REDIS_CODE_PREFIX +phoneNum;

            ResultEntity<String> saveCodeResultEntity = redisRemoteService.setRedisKeyValueRemoteWithTimeOut(key, code, 5, TimeUnit.MINUTES);

            if(ResultEntity.SUCCESS.equals(saveCodeResultEntity.getResult())){
                return ResultEntity.successWithoutData();
            }else {
                return saveCodeResultEntity;
            }

        }else {
            return sendMessageResultEntity;
        }

    }



}
