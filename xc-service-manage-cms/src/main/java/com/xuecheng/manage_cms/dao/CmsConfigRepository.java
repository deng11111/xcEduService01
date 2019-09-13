package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @ClassName CmsConfigRepository
 * @Author 邓元粮
 * @Date 2019/1/20 22:12
 * @Version 1.0
 **/
public interface CmsConfigRepository extends MongoRepository<CmsConfig,String> {
}
