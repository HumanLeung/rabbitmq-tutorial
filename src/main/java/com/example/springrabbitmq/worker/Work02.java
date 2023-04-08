package com.example.springrabbitmq.worker;

import com.example.springrabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Work02 {
    public static final String QUEUE_NAME = "hello";



    public static void main(String[] args) throws IOException, TimeoutException {
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println(new String(message.getBody()));
        };

        CancelCallback cancelCallback = consumerTag -> {
            System.out.println(consumerTag + "message interrupted");
        };
        Channel channel = RabbitMQUtils.getChannel();

        System.out.println("C2 is waiting for message......");
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);
    }
}
