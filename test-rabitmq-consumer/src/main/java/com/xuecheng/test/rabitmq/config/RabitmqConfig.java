package com.xuecheng.test.rabitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName RabitmqConfig
 * @Author 邓元粮
 * @Date 2019/1/30 22:59
 * @Version 1.0
 **/
@Configuration
public class RabitmqConfig {

    public static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    public static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    public static final String EXCHANGE_TOPICS_INFORM="exchange_topics_inform";
    public static final String ROUTING_KEY_EMAIL="inform.#.email.#";
    public static final String ROUTING_KEY_SMS="inform.#.sms.#";

    /**
     * 生命交换机
     * @Author 邓元粮
     * @MethodName
     * @Date 23:08 2019/1/30
     * @Param
     * @return
     **/
    @Bean(EXCHANGE_TOPICS_INFORM)
    public Exchange EXCHANGE_TOPICS_INFORM(){
        //durable(true) --- 持久化交换机，重启rabitmq后该交换机依然存在
        return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_INFORM).durable(true).build();
    }

    /**
     * 声明邮件队列
     * @Author 邓元粮
     * @MethodName
     * @Date 23:10 2019/1/30
     * @Param
     * @return
     **/
    @Bean(QUEUE_INFORM_EMAIL)
    public Queue QUEUE_INFORM_EMAIL(){
        return new Queue(QUEUE_INFORM_EMAIL);
    }

    /**
     * 声明信息队列
     * @Author 邓元粮
     * @MethodName QUEUE_INFORM_SMS
     * @Date 23:13 2019/1/30
     * @Param []
     * @return org.springframework.amqp.core.Queue
     **/
    @Bean(QUEUE_INFORM_SMS)
    public Queue QUEUE_INFORM_SMS(){
        return new Queue(QUEUE_INFORM_SMS);
    }

    /**
     *  绑定邮件--交换机
     * @Author 邓元粮
     * @MethodName binding_QUEUE_INFORM_EMAIL
     * @Date 23:15 2019/1/30
     * @Param [QUEUE_INFORM_EMAIL, EXCHANGE_TOPICS_INFORM]
     * @return org.springframework.amqp.core.Binding
     **/
    @Bean
    public Binding binding_QUEUE_INFORM_EMAIL(@Qualifier(QUEUE_INFORM_EMAIL) Queue queue ,@Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange ){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_EMAIL).noargs();
    }

    /**
     *  绑定信息--交换机
     * @Author 邓元粮
     * @MethodName binding_QUEUE_INFORM_EMAIL
     * @Date 23:15 2019/1/30
     * @Param [QUEUE_INFORM_EMAIL, EXCHANGE_TOPICS_INFORM]
     * @return org.springframework.amqp.core.Binding
     **/
    @Bean
    public Binding binding_QUEUE_INFORM_SMS(@Qualifier(QUEUE_INFORM_SMS) Queue queue ,@Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_SMS).noargs();
    }

}
