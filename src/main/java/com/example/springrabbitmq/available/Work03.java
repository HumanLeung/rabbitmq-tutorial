package com.example.springrabbitmq.available;

import com.example.springrabbitmq.utils.RabbitMQUtils;
import com.example.springrabbitmq.utils.SleepUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Work03 {
    public static final String TASK_QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        System.out.println("C1等待接受消息处理时间较短");

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            SleepUtils.sleep(1);
            System.out.println("received message: "+ new String(message.getBody(), StandardCharsets.UTF_8));

           /*
           * 1.消费的标记 tag
           * 2. 是否批量应答 false:不批量应答信道中的消费
            */

            int prefetchCount = 1;
            channel.basicQos(prefetchCount);
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
        };

        boolean autoAck = false;
        channel.basicConsume(TASK_QUEUE_NAME,autoAck,deliverCallback,(consumerTag -> {
            System.out.println(consumerTag + "callback is invoked");
        }));
    }
}
