package com.bjpowernode.crm.settings.service;

import com.bjpowernode.crm.settings.domain.Dicvalue;

import java.util.List;
import java.util.Map;

public interface DicService {
    Map<String, List<Dicvalue>> getAll();
}
