package com.example.springrabbitmq.deadQueue;

import com.example.springrabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Consumer01 {
    public static final String NORMAL_EXCHANGE = "normal_exchange";
    public static final String DEAD_EXCHANGE = "dead_exchange";
    public static final String NORMAL_QUEUE = "normal_queue";
    public static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE,BuiltinExchangeType.DIRECT);

        Map<String,Object> arguments = new HashMap<>();

        arguments.put("x-message-ttl",10000);
        arguments.put("x-dead-letter-exchange",DEAD_EXCHANGE);
        arguments.put("x-dead-letter-routing-key","lisi");
        arguments.put("x-max-length",6);

        channel.queueDeclare(NORMAL_QUEUE,false,false,false,arguments);

        channel.queueBind(NORMAL_QUEUE,NORMAL_EXCHANGE,"LongSam");

        channel.queueBind(DEAD_QUEUE,DEAD_EXCHANGE,"lisi");

        DeliverCallback deliverCallback = (consumeTag, message) -> {
           String msg = new String(message.getBody(), StandardCharsets.UTF_8);
            if (msg.equals("info5")){
                System.out.println("Consumer01接收的消息是："+msg+": 此消息被C1拒绝");
                channel.basicReject(message.getEnvelope().getDeliveryTag(),false);
            }else{
                System.out.println("Consumer01接收的消息是："+msg+": 此消息被C1拒绝");
            }
        };

        channel.basicConsume(NORMAL_QUEUE,true,deliverCallback,
                consumerTag -> {});
    }
}
