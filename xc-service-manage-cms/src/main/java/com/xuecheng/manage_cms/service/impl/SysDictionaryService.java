package com.xuecheng.manage_cms.service.impl;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.dao.SysDictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName SysDictionaryService
 * @Author 邓元粮
 * @Date 2019/2/19 22:05
 * @Version 1.0
 **/
@Service
public class SysDictionaryService {
    @Autowired
    private SysDictionaryRepository sysDictionaryRepository ;


    public SysDictionary getByType(String type) {
        SysDictionary sysDictionary = sysDictionaryRepository.findByDType(type);
        return sysDictionary;
    }
}
