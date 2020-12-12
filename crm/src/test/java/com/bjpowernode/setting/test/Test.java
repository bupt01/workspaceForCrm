package com.bjpowernode.setting.test;

import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.MD5Util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {
    public static void main(String[] args) {
       //验证失效时间
       // String expireTime="2020-10-10 10:10:10";
        //当前系统时间
        //String currentTime= DateTimeUtil.getSysTime();
        //System.out.println(expireTime.compareTo(currentTime));
        //String lockState="0";
        //if ("0".equals(lockState)){
          //  System.out.println("账号已经锁定");
        //}
        //游览器端的ip地址
       /* String ip="192.168.1.3";
        //允许访问的ip地址群
        String allowIps="192.168.1.1,192.168.1.2";
        if (allowIps.contains(ip)){
            System.out.println("有效的ip地址，允许访问系统");
        }else{
            System.out.println("无效的ip地址，地址受限，请联系管理员");
        }*/
        String pwd="1234";
        String pwd1=MD5Util.getMD5(pwd);
        System.out.println(pwd1);

    }
}
