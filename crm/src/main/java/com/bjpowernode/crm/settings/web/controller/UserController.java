package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.MD5Util;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入用户控制器");
        String path=request.getServletPath();
        if ("/settings/user/login.do".equals(path)){
            login(request,response);

        }else if("/settings/user/xxx.do".equals(path)){

        }

    }

    private void login(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到验证登录的操作");
        String loginAct=request.getParameter("loginAct");
        String loginPwd=request.getParameter("loginPwd");
        //将密码的明文转换为密文
        loginPwd= MD5Util.getMD5(loginPwd);
        //接收ip地址
        String ip=request.getRemoteAddr();
        System.out.println("ip:"+ip);
        //业务层的开发统一使用代理类的接口对象
       UserService us= (UserService) ServiceFactory.getService(new UserServiceImpl());
       try{

           User user=us.login(loginAct,loginPwd,ip);
           request.getSession().setAttribute("user",user);
           System.out.println(user);
           //如果程序执行到此处，说明业务层没有为controller抛出任何的异常
           //表示登陆成功
           PrintJson.printJsonFlag(response,true);


       }catch (Exception exception){
           exception.printStackTrace();
           //一旦程序执行了catch()块的信息，说明业务层为我们验证登录失败为controller抛出了异常
           //表示登陆失败
           String msg= exception.getMessage();
           /**
            * 我们现在作为控制器需要为ajax提供多项信息
            * 可以有两种方式处理：
            *    将多项信息打包成map，将map解析为json
            *    创建一个用来展现值的对象vo
            *  如果对于信息将来得大量的使用，我们创建一个vo类，使用方便
            *  如果对于展现的信息只有在这个需求中能用，我们使用map
            */
           Map<String,Object> map=new HashMap<String,Object>();
           map.put("success",false);
           map.put("msg",msg);
           PrintJson.printJsonObj(response,map);

       }

    }
}
