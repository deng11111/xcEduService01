package com.xuecheng.manage_course.client;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * cmsPage客户端feignClient
 * @ClassName CmsPageClient
 * @Description TODO
 * @Author Administrator
 * @Date 2019/6/911:06
 * @Version 1.0
 **/
@FeignClient(value = "XC-SERVICE-MANAGE-CMS") //指定服务名
public interface CmsPageClient {

    /**
     * 根据页面id获取页面
     * @Author 邓元粮
     * @MethodName findById
     * @Date 14:21 2019/6/10
     * @Param [id]
     * @return com.xuecheng.framework.domain.cms.CmsPage
     **/
    @GetMapping("/cms/page/get/{id}")//告知请求方式及路径,同springmvc配置相同
    public CmsPage findById(@PathVariable("id") String id);

    /**
     * 保存页面接口
     * @Author 邓元粮
     * @MethodName save
     * @Date 14:20 2019/6/10
     * @Param [cmsPage]
     * @return com.xuecheng.framework.domain.cms.response.CmsPageResult
     **/
    @PostMapping("/cms/page/save")
    public CmsPageResult save(@RequestBody CmsPage cmsPage);

    @PostMapping("/cms/page/postPageQuick")
    public CmsPostPageResult postPageQuick(@RequestBody CmsPage cmsPage);



}
