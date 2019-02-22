package com.test.springamq;

import com.alibaba.fastjson.JSON;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.annotation.JmsListeners;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.List;

@Component
public  class AMQClient<T> {

    //@Resource的作用相当于@Autowired，只不过@Autowired按byType自动注入，而@Resource默认按 byName自动注入。
    //@Resource(name = "jmsTemplate")
    @Autowired
    private  JmsTemplate jmsTemplate;


    //目的地队列的明证，我们要向这个队列发送消息
    //@Resource(name = "destinationQueue")
    @Autowired
    private ActiveMQQueue destinationQueue;


    //目的地队列的明证，我们要向这个队列发送消息
    //@Resource(name = "destinationQueue")
    @Autowired
    private ActiveMQTopic destinationTopic;


    private DefaultMessageListenerContainer defaultMessageListenerContainer;

    public DefaultMessageListenerContainer getDefaultMessageListenerContainer() {
        return defaultMessageListenerContainer;
    }

    public void setDefaultMessageListenerContainer(DefaultMessageListenerContainer defaultMessageListenerContainer) {
        this.defaultMessageListenerContainer = defaultMessageListenerContainer;
    }


//    @Bean
//    public DefaultMessageListenerContainer setListener(MessageListener messageListener){
//         DefaultMessageListenerContainer defaultMessageListenerContainer =
//                 new DefaultMessageListenerContainer();
//         defaultMessageListenerContainer.setConnectionFactory(jmsTemplate.getConnectionFactory());
//         destinationTopic.setPhysicalName("spring-topic");
//         defaultMessageListenerContainer.setDestination(destinationTopic);
//
//         defaultMessageListenerContainer.setMessageListener(messageListener);
//         return defaultMessageListenerContainer;
//    }

    public void setListener(Class clazz){
        ConfigurableApplicationContext context = (ConfigurableApplicationContext)  DanyConfig.getApplicationContext();
        //Bean的实例工厂
        DefaultListableBeanFactory dbf = (DefaultListableBeanFactory) context.getBeanFactory();

        //添加Topic
        BeanDefinitionBuilder dataSourceBuider_destinationtopic = BeanDefinitionBuilder.genericBeanDefinition(ActiveMQTopic.class);
        dataSourceBuider_destinationtopic.addConstructorArgValue("TOPIC.ALL");
        dbf.registerBeanDefinition("TOPIC.ALL",dataSourceBuider_destinationtopic.getRawBeanDefinition());
        //dbf.getBean("TOPIC.ALL");

        //添加messageListener
        BeanDefinitionBuilder dataSourceBuider_messageListener = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        dbf.registerBeanDefinition("messageListener", dataSourceBuider_messageListener.getBeanDefinition());
        //dbf.getBean("messageListener");

        //添加Container
        //Bean构建  BeanService.class 要创建的Bean的Class对象
        BeanDefinitionBuilder dataSourceBuider_jmsContainer = BeanDefinitionBuilder.genericBeanDefinition(DefaultMessageListenerContainer.class);
        //向里面的属性注入值，提供get set方法
//        dataSourceBuider_jmsContainer.addPropertyValue("connectionFactory", jmsTemplate.getConnectionFactory());
//        dataSourceBuider_jmsContainer.addPropertyValue("destination", destinationTopic);
       // dataSourceBuider_jmsContainer.addPropertyValue("messageListener",dataSourceBuider_messageListener );


//        dataSourceBuider_jmsContainer.addPropertyReference("destination", "destinationTopic");
//
//        dataSourceBuider_jmsContainer.addPropertyReference("connectionFactory", "jmsFactory");

        dataSourceBuider_jmsContainer.addPropertyReference("connectionFactory", "jmsFactory");//cachingConnectionFactory
        dataSourceBuider_jmsContainer.addPropertyReference("destination", "TOPIC.ALL");
        dataSourceBuider_jmsContainer.addPropertyValue("sessionAcknowledgeMode", Session.CLIENT_ACKNOWLEDGE);
        dataSourceBuider_jmsContainer.addPropertyReference("messageListener", "messageListener");



        /*
         */
       /*try {
            dataSourceBuider_jmsContainer.addPropertyValue("messageListener",  DanyConfig.getApplicationContext().getBean("messageListener"));
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        //dataSourceBuider.setParentName("");  同配置 parent
        //dataSourceBuider_jmsContainer.setScope("");   //同配置 scope
        //将实例注册spring容器中   bs 等同于  id配置
        dbf.registerBeanDefinition("jmsContainer", dataSourceBuider_jmsContainer.getBeanDefinition());
        dbf.getBean("jmsContainer");

    }

//    public void setListener(MessageListener messageListener){
//        ConfigurableApplicationContext context = (ConfigurableApplicationContext)  DanyConfig.getApplicationContext();
//        //Bean的实例工厂
//        DefaultListableBeanFactory dbf = (DefaultListableBeanFactory) context.getBeanFactory();
//
//        this.defaultMessageListenerContainer = new DefaultMessageListenerContainer();
//        context.getBean("jmsFactory");
//        this.defaultMessageListenerContainer.setConnectionFactory((ConnectionFactory) context.getBean("jmsFactory"));
//        this.defaultMessageListenerContainer.setDestination(destinationTopic);
//        this.defaultMessageListenerContainer.setMessageListener(messageListener);

//    }

    public  void sendQueueMsg(String queueName, T t) {
        final String msg = JSON.toJSONString(t);
        try {
            //logger.info("将要向队列{}发送的消息msg:{}", destination, msg);
            //destinationQueue.setPhysicalName(queueName);
            jmsTemplate.send(destinationQueue, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createTextMessage(msg);
                }
            });
        } catch (Exception ex) {
            //logger.error("向队列{}发送消息失败，消息为：{}", destination, msg);
            ex.printStackTrace();
            System.out.println("向队列{}发送消息失败，消息为：{}"+msg);
        }
    }

    public void sendTopicMsg(String topicName,T t) {

        //重新设置Topic的名字
        //destinationTopic.setPhysicalName("TOPIC.ALL");
        ActiveMQTopic topic =  (ActiveMQTopic)DanyConfig.getApplicationContext().getBean("TOPIC.ALL");

        final String msg = JSON.toJSONString(t);
        try {
            //logger.info("将要向队列{}发送的消息msg:{}", destination, msg);
            jmsTemplate.send(topic, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createTextMessage(msg);
                }
            });
        } catch (Exception ex) {
            //logger.error("向队列{}发送消息失败，消息为：{}", destination, msg);
            ex.printStackTrace();
            System.out.println("向队列{}发送消息失败，消息为：{}"+msg);
        }
    }

    public static AMQClient getClient(){
        ApplicationContext applicationContext = null;
        applicationContext = new ClassPathXmlApplicationContext(new String[] { "spring-amq.xml"});
        AMQClient amqClient = applicationContext.getBean(AMQClient.class);
        return amqClient;
    }


}
