package com.xuecheng.test.rabitmq;

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
public class Producer01 {

    public static final String QUEUE = "helloRabitMQ";
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

        channel.queueDeclare(QUEUE,true,false,false,null);
        String message = " hello 邓元粮 !";
        //发送消息
        /**
         *String var1 --Exchange的名称，如果没有指定，则使用Default Exchange,
         *  String var2 -- routingKey,消息的路由Key，是用于Exchange（交换机）将消息转发到指定的消息队列,
         *  BasicProperties var3 --消息包含属性,
         *  byte[] var4 -- 消息内容字节数组
         **/
        channel.basicPublish("",QUEUE,null,message.getBytes("utf-8"));
        //关闭资源
        channel.close();
        connection.close();
    }
}
