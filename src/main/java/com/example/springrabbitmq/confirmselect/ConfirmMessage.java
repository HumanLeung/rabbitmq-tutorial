package com.example.springrabbitmq.confirmselect;

import com.example.springrabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;

public class ConfirmMessage {
    public static final int MESSAGE_COUNT = 1000;
    public static void main(String[] args) throws IOException, InterruptedException, TimeoutException {
        //1、单个确认
//        publicMessageIndividually();
//        publishMessageBatch();
        //2、批量确认
        //3、异步批量确认
        publishMessageAsync();
    }

    public static void publicMessageIndividually() throws IOException, TimeoutException, InterruptedException {
        Channel channel = RabbitMQUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName,true,false,false,null);
        //开启发布确认
        channel.confirmSelect();
        //开启时间
        long begin = System.currentTimeMillis();

        //批量发消息
        for(int i = 0; i < MESSAGE_COUNT; i++){
            String message = i + "";
            channel.basicPublish("",queueName,null,message.getBytes());
            //单个消息就马上进行发布消息
            boolean flag = channel.waitForConfirms();

            if (flag){
                System.out.println("消费发送成功");
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个单独确认消息，耗时" + (end - begin) + "ms");
    }

    public static void publishMessageBatch() throws IOException, TimeoutException, InterruptedException {
        Channel channel = RabbitMQUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName,true,false,false,null);
        //开启发布确认
        channel.confirmSelect();
        //开启时间
        long begin = System.currentTimeMillis();

        //批量确认消息大小
        int batchSize = 100;

        //批量发送消息 批量发布确认
        for(int i = 0; i < MESSAGE_COUNT; i++){
            String message = i + "";
            channel.basicPublish("",queueName,null,message.getBytes(StandardCharsets.UTF_8));
            //判断达到100条消息的时候批量确认一次

            if (i%batchSize == 0){
                channel.waitForConfirms();
            }

        }
        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个批量确认消息，耗时" + (end - begin) + "ms");
    }

    public static void publishMessageAsync() throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        //队列的声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName,true,false,false,null);
        channel.confirmSelect();
        long begin = System.currentTimeMillis();

        /*
        线程安全有序的一个哈希表，适用于高并发的情况下
        1.轻松的将序号与消息进行关联
        2.轻松批量删除条目 只要给到序号
        3.高并发
         */
        ConcurrentSkipListMap<Long,String> outstandingConfirms = new ConcurrentSkipListMap<>();


        //消息确认成功 回调函数
        ConfirmCallback ackCallback = (deliveryTag, multiple) -> {
            if (multiple){
                ConcurrentNavigableMap<Long,String> confirmed =
                        outstandingConfirms.headMap(deliveryTag);
                confirmed.clear();
            }else{
                outstandingConfirms.remove(deliveryTag);
            }
            System.out.println("确认的消息：" + deliveryTag);
        };

        //消息确认失败 回调函数
        /**
         * 1.消息的标记
         * 2.是否为批量确认
         */

        ConfirmCallback nackCallback = (deliveryTag, multiple) -> {
            String message = outstandingConfirms.get(deliveryTag);
            System.out.println("未确认消息是："+message+"未确认的消息："+deliveryTag);
        };
        //准备消息的监听器 监听哪些消息成功乐 哪些消息失败了
        /**
         * 1.监听哪些消息成功了
         * 2.监听哪些消息失败了
         */
        channel.addConfirmListener(ackCallback,nackCallback);

        for (int i = 0; i < MESSAGE_COUNT; i++){
            String message = "消息" + i;
            channel.basicPublish("",queueName,null,message.getBytes(StandardCharsets.UTF_8));
            // 1:此处记录下所有要发送的消息 消息的总和
            outstandingConfirms.put(channel.getNextPublishSeqNo(),message);
        }

        long end = System.currentTimeMillis();
        System.out.println("发布"+ MESSAGE_COUNT +"个异步发布确认消息，消耗"+(end - begin)+"ms");
    }
}
