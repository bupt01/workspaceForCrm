package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.dao.CustomerDao;
import com.bjpowernode.crm.workbench.dao.TranDao;
import com.bjpowernode.crm.workbench.dao.TranHistoryDao;
import com.bjpowernode.crm.workbench.domain.Customer;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.TranService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranServiceImpl implements TranService {
    private TranDao tranDao= SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao= SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private CustomerDao customerDao= SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    @Override
    public boolean save(Tran tran, String customerName) {
        /*
        交易添加业务：
        在做添加之前，参数t里面就少了一项信息，就是客户的主键，customerId
        先处理客户相关的需求：
         1、判断customerName，根据客户名称在客户表进行精确查询
         如果有这个客户，则取出这个客户的id，封装到t对象中
         如果没有这个客户则在客户表中新建一个信息，然后将新建的客户id取出，封装到t对象中
         2、经过以上操作，t对象的信息就全了，需要执行添加交易的操作
         3、添加交易完毕后，需要创建一条交易历史

         */
        boolean flag=true;
        Customer cus=customerDao.getCustomerByName(customerName);
        if (cus==null){
            cus=new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setName(customerName);
            cus.setCreateBy(tran.getCreateBy());
            cus.setCreateTime(DateTimeUtil.getSysTime());
            cus.setContactSummary(tran.getContactSummary());
            cus.setNextContactTime(tran.getNextContactTime());
            cus.setOwner(tran.getOwner());
            int count1=customerDao.save(cus);
            if (count1!=1){
                flag=false;
            }

        }
        //通过以上对于客户的处理，不论是查询出来已经有的客户，还是以前没有我们新增的
        //客户，总之客户已经有了将客户的id封装到对象
        tran.setCustomerId(cus.getId());
        //添加交易
        int count2=tranDao.save(tran);
        if (count2!=1){
            flag=false;
        }
        //添加交易历史
        TranHistory th=new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setTranId(tran.getId());
        th.setStage(tran.getStage());
        th.setMoney(tran.getMoney());
        th.setExpectedDate(tran.getExpectedDate());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setCreateBy(tran.getCreateBy());
        int count3=tranHistoryDao.save(th);
        if (count3!=1){
           flag=false;
        }



        return flag;
    }

    @Override
    public Tran detail(String id) {
        Tran t=tranDao.detail(id);
        return t;
    }

    @Override
    public List<TranHistory> getHistoryListByTranId(String tranId) {
        List<TranHistory> tranHistoryList=tranHistoryDao.getHistoryListByTranId(tranId);
        return tranHistoryList;
    }

    @Override
    public boolean changeStage(Tran t) {
        boolean flag=true;
        //改变交易阶段
        int count1=tranDao.changeStage(t);
        if (count1!=1){
            flag=false;
        }
        //交易阶段改变后，我们生成一条交易历史

        TranHistory tranHistory=new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setCreateBy(t.getEditBy());
        tranHistory.setCreateTime(DateTimeUtil.getSysTime());
        tranHistory.setExpectedDate(t.getExpectedDate());
        tranHistory.setMoney(t.getMoney());
        tranHistory.setTranId(t.getId());
        //添加交易历史
        int count2=tranHistoryDao.save(tranHistory);
        if (count2!=1){
            flag=false;
        }

        return flag;
    }

    @Override
    public Map<String, Object> getCharts() {
        //取得total
        int total=tranDao.getTotal();
        //取得dataList
        List<Map<String,Object>> dataList=tranDao.getCharts();
        //将两者保存到map中
        Map<String,Object> map=new HashMap();
        map.put("total",total);
        map.put("dataList",dataList);
        return map;
    }
}
