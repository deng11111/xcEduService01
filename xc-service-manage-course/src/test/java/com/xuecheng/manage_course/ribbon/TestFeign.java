package com.xuecheng.manage_course.ribbon;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_course.client.CmsPageClient;
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
public class TestFeign {

    @Autowired
    private CmsPageClient cmsPageClient ;
    @Test
    public void testRibbon(){
        for (int i = 0; i <10 ; i++) {
            CmsPage cmsPage = cmsPageClient.findById("5a754adf6abb500ad05688d9");
            System.out.println(cmsPage);
        }
    }
}
