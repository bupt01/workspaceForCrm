package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.domain.*;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.ClueService;
import com.bjpowernode.crm.workbench.service.CustomerService;
import com.bjpowernode.crm.workbench.service.TranService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.ClueServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.CustomerServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.TranServiceImpl;
import com.sun.org.apache.regexp.internal.RE;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入交易控制器控制器");
        String path=request.getServletPath();
        if ("/workbench/transaction/add.do".equals(path)){
            add(request,response);
        }else if("/workbench/transaction/getCustomerName.do".equals(path)){
            getCustomerName(request,response);
        }else if ("/workbench/transaction/save.do".equals(path)){
            save(request,response);
        }else if ("/workbench/transaction/detail.do".equals(path)){
            detail(request,response);
        }else if("/workbench/transaction/getHistoryListByTranId.do".equals(path)){
            getHistoryListByTranId(request,response);
        }else if("/workbench/transaction/changeStage.do".equals(path)){
            changeStage(request,response);
        }else if("/workbench/transaction/getCharts.do".equals(path)){
            getCharts(request,response);
        }

    }

    private void getCharts(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("取得交易阶段数量的统计图表的数据");
        TranService ts= (TranService) ServiceFactory.getService(new TranServiceImpl());
        /*
        业务层为我们返回
        total
        dataList
        通过map打包以上的内容进行返回
         */
        Map<String,Object> map=ts.getCharts();
        PrintJson.printJsonObj(response,map);
    }

    private void changeStage(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行改变阶段的操作");
        String id=request.getParameter("id");
        String stage=request.getParameter("stage");
        String money=request.getParameter("money");
        String expectedDate=request.getParameter("expectedDate");
        String editTime=DateTimeUtil.getSysTime();
        String editBy=((User)request.getSession().getAttribute("user")).getName();
        Tran t=new Tran();
        t.setId(id);
        t.setStage(stage);
        t.setMoney(money);
        t.setExpectedDate(expectedDate);
        t.setEditBy(editBy);
        t.setEditTime(editTime);

        TranService ts= (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean flag=ts.changeStage(t);

        Map<String,String> pMap= (Map<String, String>) this.getServletContext().getAttribute("pMap");
        t.setPossibility(pMap.get(stage));
        Map<String,Object> map=new HashMap();
        map.put("success",flag);
        map.put("t",t);

        PrintJson.printJsonObj(response,map);


    }

    private void getHistoryListByTranId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("根据id取得相应的历史列表");
        String tranId=request.getParameter("tranId");
        TranService ts= (TranService) ServiceFactory.getService(new TranServiceImpl());
        List<TranHistory> tranHistoryList=ts.getHistoryListByTranId(tranId);
        Map<String,String> pMap= (Map<String, String>) this.getServletContext().getAttribute("pMap");
        //将交易历史列表遍历
        for(TranHistory th:tranHistoryList){
           //将每一条交易历史取出每一个阶段
           String stage=th.getStage();
           String possibility=pMap.get(stage);
          //th.set
            th.setPossibility(possibility);

        }
        PrintJson.printJsonObj(response,tranHistoryList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("跳转到详细信息页");
        String id=request.getParameter("id");
        TranService ts= (TranService) ServiceFactory.getService(new TranServiceImpl());
        Tran t=ts.detail(id);
        //处理可能性
        /*
        阶段和可能性之间的对应关系

         */
        String stage=t.getStage();
        ServletContext application1=this.getServletContext();
        //ServletContext application2=request.getServletContext();
        //ServletContext application3=this.getServletConfig().getServletContext();
        Map<String,String> pMap= (Map<String, String>) application1.getAttribute("pMap");
        String possibility=pMap.get(stage);
        //request.setAttribute("possibility",possibility);
        t.setPossibility(possibility);
        request.setAttribute("t",t);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request,response);

    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("执行添加交易的操作");
       String id=UUIDUtil.getUUID();
       String owner=request.getParameter("owner");
       String money=request.getParameter("money");
       String name=request.getParameter("name");
       String expectedDate=request.getParameter("expectedDate");
       String customerName=request.getParameter("customerName");//此时我们暂时只有客户的名称，没有id
       String stage=request.getParameter("stage");
       String type=request.getParameter("type");
       String source=request.getParameter("source");
       String activityId=request.getParameter("activityId");
       String contactsId=request.getParameter("contactsId");
       String createBy=((User)request.getSession().getAttribute("user")).getName();
       String createTime=DateTimeUtil.getSysTime();
       String description=request.getParameter("description");
       String contactSummary=request.getParameter("contactSummary");
       String nextContactTime=request.getParameter("nextContactTime");

       Tran tran=new Tran();
       tran.setId(id);
       tran.setOwner(owner);
        tran.setMoney(money);
        tran.setName(name);
        tran.setExpectedDate(expectedDate);
        tran.setStage(stage);
        tran.setType(type);
        tran.setSource(source);
        tran.setActivityId(activityId);
        tran.setContactsId(contactsId);
        tran.setCreateTime(createTime);
        tran.setCreateBy(createBy);
        tran.setDescription(description);
        tran.setContactSummary(contactSummary);
        tran.setNextContactTime(nextContactTime);

        TranService tranService= (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean flag=tranService.save(tran,customerName);
        if (flag){
            //如果添加交易成功，跳转到列表页
            //这是转发操作,但是request域内不存值，因此没必要
            //request.getRequestDispatcher("/workbench/transaction/index.jsp").forward(request,response);
            //这是重定向操作
            response.sendRedirect(request.getContextPath()+"/workbench/transaction/index.jsp");
        }

    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("取得客户名称列表（按照客户的名称进行模糊查询）");
        String name=request.getParameter("name");
        CustomerService customerService= (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        List<String> sList=customerService.getCustomerName(name);
        PrintJson.printJsonObj(response,sList);
    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到跳转到交易添加页的操作");
        UserService userService= (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> uList=userService.getUserList();
        request.setAttribute("uList",uList);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request,response);

    }

}
