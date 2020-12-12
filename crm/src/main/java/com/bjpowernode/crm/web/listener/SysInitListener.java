package com.bjpowernode.crm.web.listener;

import com.bjpowernode.crm.settings.domain.Dicvalue;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.settings.service.impl.DicServiceImpl;
import com.bjpowernode.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

/**
 * event：该参数能够取得监听的对象监听的对象是什么，就可以通过该参数获取什么
 */
public class SysInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("上下文对象");
        ServletContext application=sce.getServletContext();
        DicService ds=(DicService) ServiceFactory.getService(new DicServiceImpl());
        //应该管业务层要什么？7个list呢
        //可以打包成map，
        Map<String, List<Dicvalue>> map=ds.getAll();
        //将map解析为上下文中对象中保存的键值对
        Set<String> set=map.keySet();
        for (String key:set){
            application.setAttribute(key,map.get(key));
        }
        System.out.println("服务器缓存数据字典结束");
        //------------------------------------
        //数据字典处理完毕后我们需要处理stage2possibility.properties
        /*
        处理文件的步骤：
        解析该文件，将该属性文件中的键值对关系处理成为java中的键值对关系
        pMap
         */
        Map<String,String> pMap=new HashMap<>();


        ResourceBundle rb=ResourceBundle.getBundle("Stage2Possibility");
        //切记切记，不要加后缀名
        //枚举类型：
        Enumeration<String> e=rb.getKeys();
        while (e.hasMoreElements()){
            String key=e.nextElement();
            //迭代器遍历器是最快的，所以，迭代器还是很需要的。尽量使用迭代器
            //可能性
            String value=rb.getString(key);
            pMap.put(key,value);
        }
        //将pMap保存到缓存中
        application.setAttribute("pMap",pMap);


    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
