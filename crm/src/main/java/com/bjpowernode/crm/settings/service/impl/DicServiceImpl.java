package com.bjpowernode.crm.settings.service.impl;

import com.bjpowernode.crm.settings.dao.DicTypeDao;
import com.bjpowernode.crm.settings.dao.DicValueDao;
import com.bjpowernode.crm.settings.domain.DicType;
import com.bjpowernode.crm.settings.domain.Dicvalue;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DicServiceImpl implements DicService {
    private DicTypeDao dicTypeDao= SqlSessionUtil.getSqlSession().getMapper(DicTypeDao.class);
    private DicValueDao dicValueDao= SqlSessionUtil.getSqlSession().getMapper(DicValueDao.class);

    @Override
    public Map<String, List<Dicvalue>> getAll() {
        Map<String,List<Dicvalue>> map=new HashMap();
        //将字典类型列表取出
        List<DicType> dtList=dicTypeDao.getTypeList();
        //将字典类型列表遍历
        for (DicType dt:dtList){
            //取得每一种类型的字典类型的编码
            String code=dt.getCode();
            //根据每一种字典类型取得字典值列表
            List<Dicvalue> dvList=dicValueDao.getListByCode(code);
            map.put(code+"List",dvList);
        }
        return map;
    }
}
