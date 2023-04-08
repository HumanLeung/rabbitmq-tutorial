package com.example.springrabbitmq.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer {
    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("165.22.105.166");
        factory.setUsername("admin");
        factory.setPassword("qq123456");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        DeliverCallback deliverCallback = (consumerTag,message) -> {
            System.out.println(new String(message.getBody()));
        };

        CancelCallback cancelCallback = consumerTag -> {
            System.out.println("message interrupted");
        };


        /*
        消费者消费消息
        1.消费哪个队列
        2.消费成功之后是否要自动应答true代表的自动应答false代表手动应答
        3.消费者未成功消费的回到
        4.消费者取消消费的回调
         */

        channel.basicConsume(QUEUE_NAME, true,deliverCallback,cancelCallback);
    }
}
