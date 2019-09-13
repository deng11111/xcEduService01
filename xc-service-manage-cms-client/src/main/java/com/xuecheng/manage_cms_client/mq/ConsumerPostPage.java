package com.xuecheng.manage_cms_client.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms_client.service.PageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName ConsumerPostPage
 * @Author 邓元粮
 * @Date 2019/2/5 11:22
 * @Version 1.0
 **/
@Component
public class ConsumerPostPage {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerPostPage.class);

    @Autowired
    private PageService pageService ;

    @RabbitListener(queues={"${xuecheng.mq.queue}"})
    public void postPage(String msg){
        //解析消息
        Map map = JSON.parseObject(msg, Map.class);
        String pageId = (String) map.get("pageId");
        //校验页面
        CmsPage cmsPage = pageService.findCmsPageById(pageId);
        if(cmsPage == null){
            logger.error("cmsPage is null ,pageId:{}",pageId);
            return;
        }
        pageService.savePageToServerPath(pageId);
    }
}
