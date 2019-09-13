package com.xuecheng.manage_course.ribbon;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

/**
 * @ClassName TestRibbon
 * @Author 邓元粮
 * @Date 2019/6/9 10:07
 * @Version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRibbon {
    @Autowired
    private RestTemplate restTemplate;
    @Test
    public void testRibbon(){
        //获取服务名
        String serviceId = "XC-SERVICE-MANAGE-CMS";
        for (int i = 0; i < 10; i++) {
            String url = "http://"+serviceId+"/cms/page/get/5a754adf6abb500ad05688d9";
            ResponseEntity<CmsPage> entity = restTemplate.getForEntity(url, CmsPage.class);
            CmsPage cmsPage = entity.getBody();
            System.out.println(cmsPage);
        }
    }
}
