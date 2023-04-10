package com.no.crowd.util;

import com.no.crowd.constant.CrowdConstant;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import java.net.URL;
import java.util.Map;

/**
 * 尚筹网的通用工具类
 * @author NO
 * @create 2022-06-10-14:53
 */
public class CrowdUtil {

    /**
     *
     * @param host  请求的地址
     * @param path  请求的后缀
     * @param appCode   购入的api的appCode
     * @param phoneNum  发送验证码的目的号码
     * @param sign      签名编号
     * @param skin      模板编号
     * @return          发送成功则返回发送的验证码，放在ResultEntity中，失败则返回失败的ResultEntity
     */
    public static ResultEntity<String> sendCodeByShortMessage(
            String host,
            String path,
            String appCode,
            String method,
            String phoneNum,
            String sign,
            String skin
    ){
        // 生成验证码
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 4; i++){
            int random = (int)(Math.random()*10);
            builder.append(random);
        }
        String param = builder.toString();  // 【4】请求参数，详见文档描述
        String urlSend = host + path + "?param=" + param + "&phone=" + phoneNum + "&sign=" + sign + "&skin=" + skin;  // 【5】拼接请求链接
        try {
            URL url = new URL(urlSend);
            HttpURLConnection httpURLCon = (HttpURLConnection) url.openConnection();
            httpURLCon.setRequestProperty("Authorization", "APPCODE " + appCode);// 格式Authorization:APPCODE (中间是英文空格)
            int httpCode = httpURLCon.getResponseCode();
            if (httpCode == 200) {
                String json = read(httpURLCon.getInputStream());
                System.out.println("正常请求计费(其他均不计费)");
                System.out.println("获取返回的json:");
                System.out.print(json);
                return ResultEntity.successWithData(param);
            } else {
                Map<String, List<String>> map = httpURLCon.getHeaderFields();
                String error = map.get("X-Ca-Error-Message").get(0);
                if (httpCode == 400 && error.equals("Invalid AppCode `not exists`")) {
                    return ResultEntity.failed("AppCode错误 ");
                } else if (httpCode == 400 && error.equals("Invalid Url")) {
                    return ResultEntity.failed("请求的 Method、Path 或者环境错误");
                } else if (httpCode == 400 && error.equals("Invalid Param Location")) {
                    return ResultEntity.failed("参数错误");
                } else if (httpCode == 403 && error.equals("Unauthorized")) {
                    return ResultEntity.failed("服务未被授权（或URL和Path不正确）");
                } else if (httpCode == 403 && error.equals("Quota Exhausted")) {
                    return ResultEntity.failed("套餐包次数用完 ");
                } else {
                    return ResultEntity.failed("参数名错误 或 其他错误" + error);
                }
            }

        } catch (MalformedURLException e) {
            return ResultEntity.failed("URL格式错误");
        } catch (UnknownHostException e) {
            return ResultEntity.failed("URL地址错误");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.failed("套餐包次数用完 ");
        }
    }


    /*
     * 读取返回结果
     */
    private static String read(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = br.readLine()) != null) {
            line = new String(line.getBytes(), StandardCharsets.UTF_8);
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }


    /**
     * 对明文字符串进行加密
     * @param source 传入的明文
     * @return 明文加密后的字符串
     */
    public static String md5(String source) {
        // 判断source是否有效
        if (source == null || source.length() == 0) {
            //如果是空字符串，抛出异常
            throw  new RuntimeException(CrowdConstant.MESSAGE_STRING_INVALIDATE);
        }

        try {
            // 获取MessageDigest对象
            String  algorithm = "md5";

            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);

            // 获取明文字符串对应的字节数组
            byte[] input = source.getBytes();

            // 进行加密
            byte[] output = messageDigest.digest(input);

            // 创建BigInteger对象
            int signum = 1;
            BigInteger bigInteger = new BigInteger(1, output);

            //按照16进制将bigInteger的值转化为字符串
            int radix =16;
            String encoded = bigInteger.toString(radix).toUpperCase(Locale.ROOT);

            return  encoded;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 判断请求类型的工具方法
     *
     * @param request
     * @return true 是Ajax请求，false 不是Ajax请求
     */
    public static boolean judgeRequestType(HttpServletRequest request) {

        //获取请求消息头
        String acceptHeader = request.getHeader("Accept");
        String XRequestHeader = request.getHeader("X-Requested-With");

        //判端
        return (
                acceptHeader != null
                        &&
                        acceptHeader.length() > 0
                        &&
                        acceptHeader.contains("application/json")
        )
                ||
                (
                        XRequestHeader != null
                                &&
                                XRequestHeader.length() > 0
                                &&
                                XRequestHeader.equals("XMLHttpRequest")
                );

    }
}
