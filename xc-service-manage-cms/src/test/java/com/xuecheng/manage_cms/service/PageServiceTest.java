package com.xuecheng.manage_cms.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName PageServiceTest
 * @Author 邓元粮
 * @Date 2019/1/27 14:23
 * @Version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class PageServiceTest {

    @Autowired
    private PageService pageServiceImpl;

    @Test
    public void testGetHtml(){
        String content = pageServiceImpl.getPageHtml("5c440edaa14d7a3684fdc050");
        System.out.println(content);
    }
}
