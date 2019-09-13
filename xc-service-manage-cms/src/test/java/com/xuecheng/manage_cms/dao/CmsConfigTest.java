package com.xuecheng.manage_cms.dao;

import okhttp3.OkHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @ClassName CmsConfigTest
 * @Author 邓元粮
 * @Date 2019/1/20 22:23
 * @Version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsConfigTest {

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void testOkHttpClient(){
        ResponseEntity<Map> response = restTemplate.getForEntity("http://localhost:31001/cms/config/getmodel/5a791725dd573c3574ee333f", Map.class);
        Map body = response.getBody();
    }
}
