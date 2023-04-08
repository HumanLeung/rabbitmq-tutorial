package com.example.springrabbitmq.exchange;

import com.example.springrabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class ReceiveLogs02 {

    public static final String EXCHANGE_MAME = "logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();

        //声明一个交换机
       channel.exchangeDeclare(EXCHANGE_MAME,"fanout");
        //声明一个队列、队列的名称是随机的
        /*
         * 生成一个临时队列、队列的名称是随机的
         * 当消费者断开与队列的链接的时候 队列就自动删除
         */
        String queueName =  channel.queueDeclare().getQueue();

        channel.queueBind(queueName, EXCHANGE_MAME,"");
        System.out.println("等待接受消息， 把接收到消息打印在屏幕上.........");

        //接收消息
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("ReceiveLogs02----控制台打印接收到的消息："+new String(message.getBody(), StandardCharsets.UTF_8));
        };

        //消费者取消消费时回调接口
        channel.basicConsume(queueName,true,deliverCallback, consumerTag -> {});
    }
}
