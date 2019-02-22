package com.test.springamq;

import org.springframework.jms.listener.DefaultMessageListenerContainer;

public class Test {
    public static void main(String[] args) {
        User user = new User();
        user.setId(1);
        user.setName("name");
        user.setAge(1);
        AMQClient amqClient = AMQClient.getClient();

        amqClient.setListener(MyTopicMessageListener.class);
//        amqClient.setListener(new MyTopicMessageListener());
//        amqClient.getDefaultMessageListenerContainer().start();

//        amqClient.sendQueueMsg("spring-queue",user);

        amqClient.sendTopicMsg("spring-topic",user);
    }
}
