package com.xuecheng.manage_cms_client.repository;

import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @ClassName CmsSiteRepository
 * @Author 邓元粮
 * @Date 2019/2/5 10:10
 * @Version 1.0
 **/
public interface CmsSiteRepository extends MongoRepository<CmsSite,String> {
}
