package com.xuecheng.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import javax.annotation.Resource;

/**
 * @ClassName GovernCenterApplication
 * @Author 邓元粮
 * @Date 2019/6/8 17:18
 * @Version 1.0
 **/
@EnableEurekaServer//标识这是一个Eureka服务,此配置不加项目启动会报404
@SpringBootApplication
public class GovernCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(GovernCenterApplication.class,args);
    }
}
