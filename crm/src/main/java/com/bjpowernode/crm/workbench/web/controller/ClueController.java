package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.ClueService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClueController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入线索控制器");
        String path=request.getServletPath();
        if ("/workbench/clue/getUserList.do".equals(path)){
            getUserList(request,response);


        }else if("/workbench/clue/save.do".equals(path)){

            save(request,response);
        }else if("/workbench/clue/detail.do".equals(path)){
            detail(request,response);
            
        }else if("/workbench/clue/getActivityListByClueId.do".equals(path)){
            getActivityListByClueId(request,response);

        }else if("/workbench/clue/unbund.do".equals(path)){
            unbund(request,response);

        }else if ("/workbench/clue/getActivityListByNameAndNotByClueId.do".equals(path)){
            getActivityListByNameAndNotByClueId(request,response);

        }else if("/workbench/clue/bund.do".equals(path)){
            bund(request,response);

        }else if("/workbench/clue/getActivityListByName.do".equals(path)){
            getActivityListByName(request,response);

        }else if("/workbench/clue/convert.do".equals(path)){
            convert(request,response);

        }

    }
    private  void convert(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("需要转换的操作");
        String clueId=request.getParameter("clueId");
        //接收是否需要创建交易的标记
        String flag=request.getParameter("flag");
        String createBy=((User)request.getSession().getAttribute("user")).getName();
        Tran t=null;
        //如果需要创建交易
        if ("a".equals(flag)){
            t=new Tran();
            //接收表单中的参数
            String money=request.getParameter("money");
            String name=request.getParameter("name");
            String expectedDate=request.getParameter("expectedDate");
            String stage=request.getParameter("stage");
            String activityId=request.getParameter("activityId");
            String id=UUIDUtil.getUUID();
            String createTime=DateTimeUtil.getSysTime();

            t.setId(id);
            t.setMoney(money);
            t.setName(name);
            t.setExpectedDate(expectedDate);
            t.setStage(stage);
            t.setActivityId(activityId);
            t.setCreateBy(createBy);
            t.setCreateTime(createTime);
        }
        ClueService cs= (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        /*
        为业务层传递的参数：
        1、必须通过传递的参数clueId，有了这个clueId之后我们才知道要转换哪条记录
        2、必须传递的参数t，因为在线索转换过程中，有可能临时创建一笔交易（业务层传递的也有可能是空）
         */
        boolean flag1=cs.convert(clueId,t,createBy);
        if (flag1){
            response.sendRedirect(request.getContextPath()+"/workbench/clue/index.jsp");

        }



    }

    private void getActivityListByName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("查询市场活动列表根据名称模糊查询");
        String aname=request.getParameter("aname");
        ActivityService activityService= (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> aList=activityService.getActivityListByName(aname);
        PrintJson.printJsonObj(response,aList);
    }

    private void bund(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行关联市场活动的操作");
        String cid=request.getParameter("cid");
        String aids[] =request.getParameterValues("aid");
        ClueService cs= (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag=cs.bund(cid,aids);
        PrintJson.printJsonFlag(response,flag);


    }

    private void getActivityListByNameAndNotByClueId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("根据名称模糊查，排除已经关联指定的线索");
        String aname=request.getParameter("aname");
        String clueId=request.getParameter("clueId");
        Map<String,String> map=new HashMap();
        map.put("aname",aname);
        map.put("clueId",clueId);
        ActivityService activityService= (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> aList=activityService.getActivityListByNameAndNotByClueId(map);
        PrintJson.printJsonObj(response,aList);

    }

    private void unbund(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行解除关联操作");
        String id=request.getParameter("id");
        ClueService cs= (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag=cs.unbund(id);
        PrintJson.printJsonFlag(response,flag);


    }

    private void getActivityListByClueId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("根据线索id查询到关联的市场活动列表");
        String clueId=request.getParameter("clueId");
        ActivityService activityService= (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> aList=activityService.getActivityListByClueId(clueId);
        PrintJson.printJsonObj(response,aList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("跳转到线索的详细信息页");
        String id=request.getParameter("id");
        ClueService cs= (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        Clue clue=cs.detail(id);
        request.setAttribute("c",clue);
        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request,response);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行线索的添加操作");
       String id= UUIDUtil.getUUID();
       String fullname=request.getParameter("fullname");
       String appellation=request.getParameter("appellation");
       String owner=request.getParameter("owner");
       String company=request.getParameter("company");
       String job=request.getParameter("job");
       String email=request.getParameter("email");
       String phone=request.getParameter("phone");
       String website=request.getParameter("website");
       String mphone=request.getParameter("mphone");
       String state=request.getParameter("state");
       String source=request.getParameter("source");
       String createBy=((User)request.getSession().getAttribute("user")).getName();
       String createTime=DateTimeUtil.getSysTime();
       String description=request.getParameter("description");
       String contactSummary=request.getParameter("contactSummary");
       String nextContactTime=request.getParameter("nextContactTime");
       String address=request.getParameter("address");
       Clue clue=new Clue();
       clue.setId(id);
       clue.setFullname(fullname);
       clue.setAppellation(appellation);
       clue.setOwner(owner);
       clue.setCompany(company);
       clue.setJob(job);
       clue.setEmail(email);
       clue.setPhone(phone);
       clue.setWebsite(website);
       clue.setMphone(mphone);
       clue.setState(state);
       clue.setSource(source);
       clue.setCreateBy(createBy);
       clue.setCreateTime(createTime);
       clue.setDescription(description);
       clue.setContactSummary(contactSummary);
       clue.setNextContactTime(nextContactTime);
       clue.setAddress(address);
       ClueService cs= (ClueService) ServiceFactory.getService(new ClueServiceImpl());
       boolean flag=cs.save(clue);
       PrintJson.printJsonFlag(response,flag);


    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("取得用户信息列表");
        UserService us= (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> uList=us.getUserList();
        PrintJson.printJsonObj(response,uList);
        System.out.println("结束了");
    }

}