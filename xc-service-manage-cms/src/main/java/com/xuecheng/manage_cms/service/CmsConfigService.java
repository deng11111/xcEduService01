package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsConfig;

/**
 * @ClassName CmsConfigService
 * @Description TODO
 * @Author Administrator
 * @Date 2019/1/2022:12
 * @Version 1.0
 **/
public interface CmsConfigService {
    /**
     * 根据id获取页面配置信息
     * @Author 邓元粮
     * @MethodName getConfigById
     * @Date 22:17 2019/1/20
     * @Param [id]
     * @return com.xuecheng.framework.domain.cms.CmsConfig
     **/
    CmsConfig getConfigById(String id);
}
