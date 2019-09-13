package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;

/**
 * @ClassName PageService
 * @Author 邓元粮
 * @Date 2019/1/10 21:52
 * @Version 1.0
 **/
public interface PageService {
    /**
     * 根据查询条件queryPageRequest分页查询cmsPage的对象
     * @Author 邓元粮
     * @MethodName findList
     * @Date 21:55 2019/1/10
     * @Param [page, size, queryPageRequest]
     * @return com.xuecheng.framework.model.response.QueryResponseResult
     **/
    QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);

    /**
     *  新增页面
     * @Author 邓元粮
     * @MethodName add
     * @Date 22:07 2019/1/13
     * @Param [cmsPage]
     * @return com.xuecheng.framework.domain.cms.response.CmsPageResult
     **/
    CmsPageResult add(CmsPage cmsPage);

    /**
     * 根据id查询页面
     * @Author 邓元粮
     * @MethodName findById
     * @Date 22:04 2019/1/15
     * @Param [id]
     * @return com.xuecheng.framework.domain.cms.response.CmsPageResult
     **/
    CmsPage findById(String id);

    /**
     * 更新页面
     * @Author 邓元粮
     * @MethodName update
     * @Date 21:54 2019/1/15
     * @Param [id, cmsPage]
     * @return com.xuecheng.framework.domain.cms.response.CmsPageResult
     **/
    CmsPageResult update(String id, CmsPage cmsPage);

    /**
     * 根据id删除页面
     * @Author 邓元粮
     * @MethodName delete
     * @Date 22:08 2019/1/16
     * @Param [id]
     * @return com.xuecheng.framework.model.response.ResponseResult
     **/
    ResponseResult delete(String id);

    /**
     * 根据页面id获取html页面
     * @Author 邓元粮
     * @MethodName getPageHtml
     * @Date 21:59 2019/1/23
     * @Param [pageId]
     * @return java.lang.String
     **/
    String getPageHtml(String pageId);

    /**
     * 根据pageId发布页面
     * @Author 邓元粮
     * @MethodName postPage
     * @Date 12:33 2019/2/5
     * @Param [pageId]
     * @return com.xuecheng.framework.model.response.ResponseResult
     **/
    ResponseResult postPage(String pageId);

    /**
     * 新增或者修改页面
     * @Author 邓元粮
     * @MethodName save
     * @Date 7:19 2019/6/10
     * @Param [cmsPage]
     * @return com.xuecheng.framework.domain.cms.response.CmsPageResult
     **/
    CmsPageResult save(CmsPage cmsPage);

    /**
     * 一键发布页面
     * @Author 邓元粮
     * @MethodName postPageQuick
     * @Date 11:16 2019/6/10
     * @Param [cmsPage]
     * @return com.xuecheng.framework.domain.cms.response.CmsPostPageResult
     **/
    CmsPostPageResult postPageQuick(CmsPage cmsPage);
}
