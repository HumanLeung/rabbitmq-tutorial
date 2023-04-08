package com.example.springrabbitmq.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Producer {
    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factor = new ConnectionFactory();
        factor.setHost("165.22.105.166");
        factor.setUsername("admin");
        factor.setPassword("qq123456");

        Connection connection = factor.newConnection();

        Channel channel = connection.createChannel();

        /*
         * 生成一个队列
         * 1.队列名称
         * 2.队列里面的小时是否持久化
         * 3.该队列是否提供一个消费者进行消费 是否进行消息共享
         * 4.是否自动删除 最后一个消费者断开链接以后，改队列是否自动删除
         * 5 其他参数
         */

        channel.queueDeclare(QUEUE_NAME,false,false,false,null);

        String message = "hello world"; // first try

        /*
        * 发送一个消费
        * 1.发送到哪个交换机
        * 2.路由的Key值是哪个 本次是队列的名称
        * 3. 其次参数信息
         */

        channel.basicPublish("",QUEUE_NAME,null,message.getBytes(StandardCharsets.UTF_8));
        System.out.println("done!");
    }
}
