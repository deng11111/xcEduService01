package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @ClassName CmsTemplateRepository
 * @Author 邓元粮
 * @Date 2019/1/9 22:27
 * @Version 1.0
 **/
public interface CmsTemplateRepository extends MongoRepository<CmsTemplate,String> {




}
