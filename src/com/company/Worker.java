package com.company;

import com.rabbitmq.client.*;

import java.io.IOException;

public class Worker implements Runnable {

    public static int counter = 0;

    @Override
    public void run() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.basicQos(1);

            final Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println(" [x] Received '" + message + "'");
                    try {
                        System.out.println(this);
                        Worker.counter++;
                        doWork(message);
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        System.out.println(" [x] Done");
                    }
                }
            };
            boolean autoAck = false;
            channel.basicConsume("sergey", autoAck, consumer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void doWork(String task) throws InterruptedException {
        Thread.sleep(100000);
    }
}
