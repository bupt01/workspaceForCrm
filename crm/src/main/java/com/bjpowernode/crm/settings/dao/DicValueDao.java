package com.bjpowernode.crm.settings.dao;

import com.bjpowernode.crm.settings.domain.Dicvalue;

import java.util.List;

public interface DicValueDao {
    List<Dicvalue> getListByCode(String code);
}
