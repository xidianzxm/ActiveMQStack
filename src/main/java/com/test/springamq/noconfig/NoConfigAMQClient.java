package com.test.springamq.noconfig;


import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class NoConfigAMQClient {

    public String brokerUrl;

    public String queueName;

    public String topicName;


    @Autowired
    private JmsTemplate jmsTemplate;

    private final ConfigurableApplicationContext applicationContext;

    public NoConfigAMQClient(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }







//    public static AMQClient getQueueConsumer(String brokeUrl,String queueName){
//        initClient();
//        return new AMQClient(brokeUrl);
//    }
//
//    public static AMQClient getTopicConsumer(String brokerUrl,String topicName){
//        return  new
//    }


//    public static AMQClient getQueueProducer(String brokeUrl,String queueName){
//        //塞入一个新的AMQClient name=brokerurl
//        initClient();
//        return new AMQClient(brokeUrl);
//    }

//    public static AMQClient getTopicProducer(String brokerUrl,String topicName){
//        return  new
//    }
}
