package com.xuecheng.test.rabitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * rabitMQ生产者测试
 * @ClassName Producer
 * @Author 邓元粮
 * @Date 2019/1/27 17:02
 * @Version 1.0
 **/
public class Producer03_topics {

    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    private static final String EXCHANGE_TOPICS_INFORM="exchange_topics_inform";
    private static final String ROUTING_KEY_EMAIL="inform.#.email.#";
    private static final String ROUTING_KEY_SMS="inform.#.sms.#";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = null;
        Channel channel = null ;
        //创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");connectionFactory.setVirtualHost("/");
        //获取连接
        connection = connectionFactory.newConnection();

        //获取通道
        channel = connection.createChannel();

        /**
         * 声明队列
         * String queue --队列名称,
         * boolean var2  --是否持久化,
         * boolean var3 -- 是否独占,
         * boolean var4 -- 队列不再使用时是否自动删除此队列,
         * Map<String, Object> var5 -- 其他参数
         **/

        channel.queueDeclare(QUEUE_INFORM_EMAIL,true,false,false,null);
        channel.queueDeclare(QUEUE_INFORM_SMS,true,false,false,null);

        //声明交换机
        /**
         * FANOUT:发布订阅模式
         * DIRECT:路由模式
         * HEADERS:headers模式(好像废话)
         * TOPIC:topics模式(废话)
         **/
        channel.exchangeDeclare(EXCHANGE_TOPICS_INFORM, BuiltinExchangeType.TOPIC);

        //交换机队列绑定
        /**
         * 1.queue -- 队列名称
         * 2.exchange--交换机名称
         * 3.routingkey :路由key,根据路由key向指定队列发送消息,发布/订阅模式此参数无需指定
         **/
        channel.queueBind(  QUEUE_INFORM_EMAIL,EXCHANGE_TOPICS_INFORM,ROUTING_KEY_EMAIL);
        channel.queueBind(  QUEUE_INFORM_SMS,EXCHANGE_TOPICS_INFORM,ROUTING_KEY_SMS);
        String message = "";
        for (int i = 0; i < 5; i++) {
            message  = " send email Message to User !"+ i;
            channel.basicPublish(EXCHANGE_TOPICS_INFORM,"inform.email",null,message.getBytes("utf-8"));
        }
        for (int i = 0; i < 5; i++) {
            message  = " send sms Message to User !"+ i;
            channel.basicPublish(EXCHANGE_TOPICS_INFORM,"inform.sms",null,message.getBytes("utf-8"));
        }
        for (int i = 0; i < 5; i++) {
            message  = " send email and sms Message to User !"+ i;
            channel.basicPublish(EXCHANGE_TOPICS_INFORM,"inform.email.sms",null,message.getBytes("utf-8"));
        }
        //关闭资源
        channel.close();
        connection.close();
    }
}
