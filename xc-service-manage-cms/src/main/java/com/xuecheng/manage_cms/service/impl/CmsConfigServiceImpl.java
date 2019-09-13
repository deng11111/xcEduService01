package com.xuecheng.manage_cms.service.impl;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import com.xuecheng.manage_cms.service.CmsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @ClassName CmsConfigServiceImpl
 * @Author 邓元粮
 * @Date 2019/1/20 22:12
 * @Version 1.0
 **/
@Service
public class CmsConfigServiceImpl implements CmsConfigService {
    @Autowired
    private CmsConfigRepository configRepository;
    @Override
    public CmsConfig getConfigById(String id) {
        Optional<CmsConfig> optional = configRepository.findById(id);
        if (optional.isPresent()){
            return optional.get();
        }
        return null;
    }
}
