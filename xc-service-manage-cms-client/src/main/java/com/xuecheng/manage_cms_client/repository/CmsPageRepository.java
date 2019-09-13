package com.xuecheng.manage_cms_client.repository;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @ClassName CmsPageRepository
 * @Author 邓元粮
 * @Date 2019/2/5 10:09
 * @Version 1.0
 **/
public interface CmsPageRepository extends MongoRepository<CmsPage,String> {
}
