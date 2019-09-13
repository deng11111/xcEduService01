package com.xuecheng.test.rabitmq;

import com.xuecheng.test.rabitmq.config.RabitmqConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * rabitMQ生产者测试---springboot方式
 * @ClassName Producer
 * @Author 邓元粮
 * @Date 2019/1/27 17:02
 * @Version 1.0
 *
 * 注:开始因为包名不对,导致rabbitmq无法autowired/
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class Producer03_topics_springboot {

    @Autowired
    private RabbitTemplate  rabbitTemplate;

    @Test
    public void testProducer01(){
        String message = "send email to user message by springboot";
        /**
         * 1.交换机名称
         * 2.路由key0
         * 3.消息体
         **/
        rabbitTemplate.convertAndSend(RabitmqConfig.EXCHANGE_TOPICS_INFORM,RabitmqConfig.ROUTING_KEY_EMAIL,message);
    }

}
