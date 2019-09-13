package com.xuecheng.test.rabitmq.mq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.xuecheng.test.rabitmq.config.RabitmqConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @ClassName ReceiveHandler
 * @Author 邓元粮
 * @Date 2019/2/4 11:48
 * @Version 1.0
 **/
@Component
public class ReceiveHandler {

    @RabbitListener(queues = {RabitmqConfig.QUEUE_INFORM_EMAIL})
    public void recive_email(String msg , Message message, Channel channel){
        System.out.println(msg);
    }
    @RabbitListener(queues = {RabitmqConfig.QUEUE_INFORM_SMS})
    public void recive_sms(String msg , Message message, Channel channel){
        System.out.println(msg);
    }
}
