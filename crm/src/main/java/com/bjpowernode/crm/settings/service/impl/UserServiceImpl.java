package com.bjpowernode.crm.settings.service.impl;

import com.bjpowernode.crm.exception.LoginException;
import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {
    private UserDao userDao= SqlSessionUtil.
            getSqlSession().getMapper(UserDao.class);

    @Override
    public List<User> getUserList() {
        List<User> uList=userDao.getUserList();
        return uList;
    }

    public User login(String loginAct, String loginPwd, String ip) throws LoginException {
        Map<String,String> map=new HashMap<String, String>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);
        User user=userDao.login(map);
        if (user==null){
            throw new LoginException("账号密码错误");
        }
        //如果程序能够成功的执行到该行，说明账号密码是正确的
        //需要继续向下验证其他三项信息
        //验证失效时间
        String expireTime=user.getExpireTime();
        String currentTime= DateTimeUtil.getSysTime();
        if (expireTime.compareTo(currentTime)<0){
            throw  new LoginException("账号已经失效");
        }
        String lockstate=user.getLockState();
        if ("1".equals(lockstate)){
            throw  new LoginException("账号已经锁定");
        }
        //判断ip地址
        String allowIps=user.getAllowIps();
        if (!allowIps.contains(ip)){
            throw new LoginException("ip地址受限，请联系管理员");
        }
        return user;
    }
}
