package com.xuecheng.manage_cms.service.impl;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import com.xuecheng.manage_cms.service.PageService;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @ClassName PageServiceImpl
 * @Author 邓元粮
 * @Date 2019/1/10 21:53
 * @Version 1.0
 **/
@Service
public class PageServiceImpl implements PageService {

    private static final Logger logger = LoggerFactory.getLogger(PageServiceImpl.class);

    @Autowired
    private CmsPageRepository cmsPageRepository ;

    @Autowired
    private RestTemplate restTemplate ;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate ;

    @Autowired
    private CmsSiteRepository cmsSiteRepository ;

    @Override
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        queryPageRequest = queryPageRequest == null ? new QueryPageRequest():queryPageRequest;
        CmsPage requestCmsPage = new CmsPage();
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        if (StringUtils.isNotBlank(queryPageRequest.getSiteId()) ){
            requestCmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        if (StringUtils.isNotBlank(queryPageRequest.getTemplateId())){
            requestCmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }
        if (StringUtils.isNotBlank(queryPageRequest.getPageAliase())){
            requestCmsPage.setPageAliase(queryPageRequest.getPageAliase());
            exampleMatcher = exampleMatcher.withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
        }
        Example<CmsPage> example = Example.of(requestCmsPage,exampleMatcher);
        page = page <=0 ? 0:page -1 ;
        size = size <= 0 ? 10 :size ;
        Pageable pageable = PageRequest.of(page,size);
        Page<CmsPage> cmsPage = cmsPageRepository.findAll(example,pageable);
        if ( null != cmsPage ){
            QueryResult<CmsPage> queryResult = new QueryResult<CmsPage>();
            queryResult.setList(cmsPage.getContent());
            queryResult.setTotal(cmsPage.getTotalElements());
            QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);
            return queryResponseResult;
        }
        return null;
    }

    @Override
    public CmsPageResult add(CmsPage cmsPage) {
        List<CmsPage> page = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (null != page & page.size() > 0 ){
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTS);
        }
        cmsPage.setPageId(null);
        CmsPage save = cmsPageRepository.save(cmsPage);
        return new CmsPageResult(CommonCode.SUCCESS,save);
    }

    @Override
    public CmsPage findById(String id) {
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if (optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    @Override
    public CmsPageResult update(String id, CmsPage cmsPage) {
        CmsPage one = findById(id);
        if (one != null){
            one.setTemplateId(cmsPage.getTemplateId());
            //更新所属站点
            one.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            one.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称
            one.setPageName(cmsPage.getPageName());
            //更新访问路径
            one.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //修改dataUrl
            one.setDataUrl(cmsPage.getDataUrl());
            //执行更新
            CmsPage save = cmsPageRepository.save(one);
            if (save != null){
                return new CmsPageResult(CommonCode.SUCCESS,save);
            }
        }
        return new CmsPageResult(CommonCode.FAIL,null);
    }

    @Override
    public ResponseResult delete(String id) {
        Optional<CmsPage> one = cmsPageRepository.findById(id);
        if (one.isPresent()){
            cmsPageRepository.delete(one.get());
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    /**
     * 静态化程序获取页面的DataUrl
     * 静态化程序远程请求DataUrl获取数据模型。
     * 静态化程序获取页面的模板信息
     * 执行页面静态化
     * @Author 邓元粮
     * @MethodName getPageHtml
     * @Date 22:00 2019/1/23
     * @Param [pageId]
     * @return java.lang.String
     **/
    @Override
    public String getPageHtml(String pageId) {
        //获取数据模型
        Map model = getModelByPageId(pageId);
        if(null == model){
           ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        //静态化程序获取页面的模板信息
        String templateContent = getTemplateByPageId(pageId);
        //执行页面静态化
        String html = generateHtml(templateContent,model);
        return html;
    }

    @Override
    public ResponseResult postPage(String pageId) {
        //页面静态化
        String pageContent = getPageHtml(pageId);
        if (StringUtils.isBlank(pageContent)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        //保存页面
        CmsPage cmsPage = saveHtml(pageId,pageContent);
        //发送消息
        sendPostMessage(pageId);
        //返回
        return new ResponseResult(CommonCode.SUCCESS);
    }

    @Override
    public CmsPageResult save(CmsPage cmsPage) {
        if (cmsPage == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        List<CmsPage> cmsPages = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (!CollectionUtils.isEmpty(cmsPages)){
            return update(cmsPages.get(0).getPageId(),cmsPage);
        }
        return add(cmsPage);
    }

    @Override
    @Transactional
    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {
        //保存页面
        if (cmsPage == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        CmsPageResult cmsPageResult = this.save(cmsPage);
        if (cmsPageResult == null || !cmsPageResult.isSuccess()){
            return new CmsPostPageResult(CommonCode.FAIL,null);
        }
        CmsPage cmsPageReturn = cmsPageResult.getCmsPage();
        if (cmsPageReturn == null){
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXIST);
        }
        //页面发布
        ResponseResult responseResult = postPage(cmsPageReturn.getPageId());
        if (responseResult == null || !responseResult.isSuccess()){
            return new CmsPostPageResult(CommonCode.FAIL,null);
        }
        //页面Url= cmsSite.siteDomain+cmsSite.siteWebPath+ cmsPage.pageWebPath + cmsPage.pageName
        String siteId = cmsPageReturn.getSiteId();
        Optional<CmsSite> cmsSiteOptional = cmsSiteRepository.findById(siteId);
        if (!cmsSiteOptional.isPresent()){
            ExceptionCast.cast(CmsCode.CMS_SITE_NOT_EXISTS);
        }
        CmsSite cmsSite = cmsSiteOptional.get();
        String pageUrl = cmsSite.getSiteDomain()+cmsSite.getSiteWebPath()+cmsPageReturn.getPageWebPath()+cmsPage.getPageName();
        //获取页面信息，静态化，上传服务器，筒子MQ
        //返回pageUrl
        return new CmsPostPageResult(CommonCode.SUCCESS,pageUrl);
    }

    /**
     * 发送消息
     * @Author 邓元粮
     * @MethodName sendPostMessage
     * @Date 9:30 2019/2/6
     * @Param [pageId]
     * @return void
     **/
    private void sendPostMessage(String pageId) {
        CmsPage cmsPage = findById(pageId);
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("pageId",pageId);

        String msg = JSON.toJSONString(params);
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,cmsPage.getSiteId(),msg);
    }

    /**
     * 保存静态化页面
     * @Author 邓元粮
     * @MethodName saveHtml
     * @Date 9:29 2019/2/6
     * @Param [pageId, pageContent]
     * @return com.xuecheng.framework.domain.cms.CmsPage
     **/
    private CmsPage saveHtml(String pageId, String pageContent) {

        //获取静态页面
        CmsPage cmsPage = findById(pageId);
        InputStream inputStream = null ;
        try {
            inputStream = IOUtils.toInputStream(pageContent, "utf-8");
            //删除原有页面
            String htmlFileId  = cmsPage.getHtmlFileId();
            if (StringUtils.isNotBlank(htmlFileId)){
                gridFsTemplate.delete(Query.query(Criteria.where("_id").is(htmlFileId)));
            }
            //存储页面
            ObjectId objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
            cmsPage.setHtmlFileId(objectId.toString());
            cmsPageRepository.save(cmsPage);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cmsPage;
    }

    /**
     * 根据模板以及数据模型生成静态化页面
     * @Author 邓元粮
     * @MethodName generateHtml
     * @Date 22:33 2019/1/23
     * @Param [templateContent, model]
     * @return java.lang.String
     **/
    private String generateHtml(String templateContent, Map model) {
        try {
            //创建配置对象
            Configuration configuration = new Configuration(Configuration.getVersion());
            //创建类加载器
            StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
            //类加载器加载模板
            stringTemplateLoader.putTemplate("template",templateContent);
            //配置信息配置类加载器
            configuration.setTemplateLoader(stringTemplateLoader);
            //配置对象根据模板名称获取模板
            Template template = configuration.getTemplate("template");
            //使用FreeMarkerTemplateUtils对象获取模板数据字符串
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            return html ;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        return null ;
    }

    /**
     * 根据也买你id获取模板信息
     * @Author 邓元粮
     * @MethodName getTemplateByPageId
     * @Date 22:15 2019/1/23
     * @Param pageId
     * @return java.lang.String
     **/
    private String getTemplateByPageId(String pageId) {
        CmsPage cmsPage = findById(pageId);
        if (null == cmsPage){
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXIST);
        }
        String templateId = cmsPage.getTemplateId();
        if (StringUtils.isNotBlank(templateId)){
            Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
            if (optional.isPresent()){
                //获取模板
                CmsTemplate cmsTemplate = optional.get();
                //获取fieldId
                String templateFileId = cmsTemplate.getTemplateFileId();
                //获取模板文件对象，应该是files里面得到
                GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
                //获取下载流
                GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
                //获取gridFs资源对象
                GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
                //将资源文件转换为string
                try {
                    String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
                    return content;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null ;
    }

    /**
     * 根据页面id获取页面数据模型
     * @Author 邓元粮
     * @MethodName getModelByPageId
     * @Date 22:12 2019/1/23
     * @Param pageId
     * @return java.util.Map
     **/
    private Map getModelByPageId(String pageId) {
        CmsPage cmsPage = findById(pageId);
        if (null == cmsPage){
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXIST);
        }
        //静态化程序远程请求DataUrl获取数据模型。
        String dataUrl = cmsPage.getDataUrl();
        if(StringUtils.isBlank(dataUrl)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        ResponseEntity<Map> entity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = entity.getBody();
        return body;
    }
}
