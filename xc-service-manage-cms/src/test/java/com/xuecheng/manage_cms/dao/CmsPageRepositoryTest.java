package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsPageParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName CmsPageRepositoryTest
 * @Author 邓元粮
 * @Date 2019/1/9 22:29
 * @Version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Test
    public void testFindAll(){
        List<CmsPage> all = cmsPageRepository.findAll();
        System.out.println(all);
    }

    @Test
    public void testFindByPage(){
        Pageable pageable = PageRequest.of(0,10);
        Page<CmsPage> list = cmsPageRepository.findAll(pageable);
        System.out.println(list);
    }

    @Test
    public void testInsert(){
        //定义实体类
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");
        cmsPage.setTemplateId("5a962b52b00ffc514038faf7");
        cmsPage.setPageName("测试页面");
        cmsPage.setPageCreateTime(new Date());
        List<CmsPageParam> cmsPageParams = new ArrayList<>();
        CmsPageParam cmsPageParam = new CmsPageParam();
        cmsPageParam.setPageParamName("param1");
        cmsPageParam.setPageParamValue("value1");
        cmsPageParams.add(cmsPageParam);
        cmsPage.setPageParams(cmsPageParams);
        cmsPageRepository.save(cmsPage);
        System.out.println(cmsPage);
    }

    @Test
    public void testUpdate(){
        Optional<CmsPage> optional = cmsPageRepository.findById("5c360875f1c60e5e8c665f4c");
        if (optional.isPresent()){
            CmsPage cmsPage = optional.get();
            cmsPage.setPageAliase("我的小demo");
            cmsPageRepository.save(cmsPage);
        }
    }

    @Test
    public void testFindAllByExample(){
        Pageable pageable = PageRequest.of(1,10);
        CmsPage cmsPage = new CmsPage();
//        cmsPage.setPageId("5a754adf6abb500ad05688d9");
        cmsPage.setPageAliase("轮播");
       ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        //exampleMatcher = exampleMatcher.withMatcher("pageId",ExampleMatcher.GenericPropertyMatchers.exact());
        exampleMatcher = exampleMatcher.withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
        Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);
        List<CmsPage> all = cmsPageRepository.findAll(example);
        System.out.println(all);
    }
}
