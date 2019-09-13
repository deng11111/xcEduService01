package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.manage_cms_client.repository.CmsPageRepository;
import com.xuecheng.manage_cms_client.repository.CmsSiteRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * @ClassName PageService
 * @Author 邓元粮
 * @Date 2019/2/5 10:08
 * @Version 1.0
 **/
@Service
public class PageService {

    @Autowired
    private CmsPageRepository cmsPageRepository ;

    @Autowired
    private CmsSiteRepository cmsSiteRepository ;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;
    /**
     * 根据页面id存储页面
     * @Author 邓元粮
     * @MethodName savePageToServerPath
     * @Date 10:14 2019/2/5
     * @Param [pageId]
     * @return void
     **/
    public void  savePageToServerPath(String pageId){
        FileOutputStream fileOutputStream = null ;
        InputStream inputStream = null ;
        try {
            //页面id获取页面文件
            //获取页面
            CmsPage cmsPage = findCmsPageById(pageId);
            //获取fieldiId
            String htmlFileId = cmsPage.getHtmlFileId();
            //获取站点信息
            String siteId = cmsPage.getSiteId();
            CmsSite cmsSite = findCmsSiteById(siteId);
            //获取下载流
            inputStream  = getInputStream(htmlFileId);
            //获取文件物理路径
            String filePath = cmsSite.getSitePhysicalPath()+cmsPage.getPagePhysicalPath()+cmsPage.getPageName();
//            String filePath = "D:\\temp\\"+cmsPage.getPageName();
            //将页面存到服务器
            fileOutputStream = new FileOutputStream(new File(filePath));
            IOUtils.copy(inputStream,fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null){
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (null != inputStream){
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据站点 id获取站点
     * @Author 邓元粮
     * @MethodName findCmsSiteById
     * @Date 11:07 2019/2/5
     * @Param [siteId]
     * @return com.xuecheng.framework.domain.cms.CmsSite
     **/
    private CmsSite findCmsSiteById(String siteId) {
        Optional<CmsSite> optional = cmsSiteRepository.findById(siteId);
        if (!optional.isPresent()){
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXIST);
        }
        return optional.get();
    }

    /**
     *
     * @Author 邓元粮
     * @MethodName findCmsPageById
     * @Date 11:02 2019/2/5
     * @Param []
     * @return com.xuecheng.framework.domain.cms.CmsPage
     **/
    public CmsPage findCmsPageById(String pageId) {
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if (!optional.isPresent()){
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXIST);
        }
        return optional.get();
    }

    /**
     * 获取页面输入流
     * @Author 邓元粮
     * @MethodName getInputStream
     * @Date 10:48 2019/2/5
     * @Param [htmlFileId]
     * @return java.io.InputStream
     **/
    private InputStream getInputStream(String htmlFileId) {
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(htmlFileId)));
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
        InputStream inputStream = null ;
        try {
            inputStream = gridFsResource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }
}

