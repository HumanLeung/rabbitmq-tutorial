package com.example.springrabbitmq.deadQueue;

import com.example.springrabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Producer {
    public static final String NORMAL_EXCHANGE = "normal_exchange";
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();

        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
                .expiration("10000").build();


        for (int i = 1; i < 11; i++){
            String message = "info" + i;
            channel.basicPublish(NORMAL_EXCHANGE,"LongSam",properties,message.getBytes(StandardCharsets.UTF_8));
        }
    }
}
