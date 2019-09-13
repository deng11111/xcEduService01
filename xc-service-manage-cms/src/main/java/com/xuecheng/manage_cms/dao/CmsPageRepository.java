package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @ClassName CmsPageRepository
 * @Author 邓元粮
 * @Date 2019/1/9 22:27
 * @Version 1.0
 **/
public interface CmsPageRepository extends MongoRepository<CmsPage,String> {

    /**
     * 根据页面名称查询
     * @param pageName
     * @return
     */
    CmsPage findByPageName(String pageName);

    /**
     * @Description  根据页面名称和类型查询
     * @Author 邓元粮
     * @MethodName findByPageNameAndPageType
     * @Date 21:21 2019/1/10
     * @Param [pageName, pageType]
     * @return com.xuecheng.framework.domain.cms.CmsPage
     **/
    CmsPage findByPageNameAndPageType(String pageName,String pageType);

    /**
     * @Description  根据站点和页面类型查询记录数
     * @Author 邓元粮
     * @MethodName countBySiteIdAndPageType
     * @Date 21:21 2019/1/10
     * @Param [siteId, pageType]
     * @return int
     **/
    int countBySiteIdAndPageType(String siteId,String pageType);

    /**
     * @Description  根据站点和页面类型分页查询
     * @Author 邓元粮
     * @MethodName findBySiteIdAndPageType
     * @Date 21:22 2019/1/10
     * @Param [siteId, pageType, pageable]
     * @return org.springframework.data.domain.Page<com.xuecheng.framework.domain.cms.CmsPage>
     **/
    Page<CmsPage> findBySiteIdAndPageType(String siteId, String pageType, Pageable pageable);

    List<CmsPage> findByPageNameAndSiteIdAndPageWebPath(String pageName, String siteId , String webPath);

}
