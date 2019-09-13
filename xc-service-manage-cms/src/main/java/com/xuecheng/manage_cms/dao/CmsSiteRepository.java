package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @ClassName CmsSiteRepository
 * @Description TODO
 * @Author Administrator
 * @Date 2019/6/1011:23
 * @Version 1.0
 **/
public interface CmsSiteRepository extends MongoRepository<CmsSite,String> {
}
