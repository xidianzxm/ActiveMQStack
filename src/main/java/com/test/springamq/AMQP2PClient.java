package com.test.springamq;

import com.alibaba.fastjson.JSON;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;


@Component
public class AMQP2PClient<T> {

    //@Resource的作用相当于@Autowired，只不过@Autowired按byType自动注入，而@Resource默认按 byName自动注入罢了。

    //@Resource(name = "jmsTemplate")
    @Autowired
    private JmsTemplate jmsTemplate;

    //目的地队列的明证，我们要向这个队列发送消息
    //@Resource(name = "destinationQueue")
    @Autowired
    private ActiveMQQueue destination;

    //向特定的队列发送消息
    //@Override
    public void sendMsg(T t) {
        final String msg = JSON.toJSONString(t);
        try {
            //logger.info("将要向队列{}发送的消息msg:{}", destination, msg);
            jmsTemplate.send(destination, new MessageCreator() {
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



}


