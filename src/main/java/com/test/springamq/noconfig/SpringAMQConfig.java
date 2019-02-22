package com.test.springamq.noconfig;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;

public class SpringAMQConfig {




    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory(String brokerUrl){
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(brokerUrl);

        return activeMQConnectionFactory;
    }

    @Bean
    public PooledConnectionFactory pooledConnectionFactory(ActiveMQConnectionFactory activeMQConnectionFactory){
        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setMaxConnections(100);
        pooledConnectionFactory.setTimeBetweenExpirationCheckMillis(3000);
        pooledConnectionFactory.setConnectionFactory(activeMQConnectionFactory);

        return  pooledConnectionFactory;
    }

    @Bean(name = "jmsTemplate")
    public JmsTemplate jmsTemplate(PooledConnectionFactory pooledConnectionFactory){
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(pooledConnectionFactory);
        jmsTemplate.setMessageConverter(new SimpleMessageConverter());

        return  jmsTemplate;
    }


    @Bean
    public ActiveMQQueue activeMQQueue(String queueName){
        return new ActiveMQQueue(queueName);
    }


}
