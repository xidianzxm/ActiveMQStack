package com.test;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/*
关于持久化和非持久化消息

持久化消息
这是 ActiveMQ 的默认传送模式，此模式保证这些消息只被传送一次和成功使用一次。对于这些消息，可靠性是优先考虑的因素。
可靠性的另一个重要方面是确保持久性消息传送至目标后，消息服务在向消费者传送它们之前不会丢失这些消息。
\这意味着在持久性消息传送至目标时，消息服务将其放入持久性数据存储。
如果消息服务由于某种原因导致失败，它可以恢复此消息并将此消息传送至相应的消费者。虽然这样增加了消息传送的开销，但却增加了可靠性。

非持久化消息
保证这些消息最多被传送一次。对于这些消息，可靠性并非主要的考虑因素。
此模式并不要求持久性的数据存储，也不保证消息服务由于某种原因导致失败后消息不会丢失。
有两种方法指定传送模式：
1．使用setDeliveryMode 方法，这样所有的消息都采用此传送模式； 如：
producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
2．使用send 方法为每一条消息设置传送模式
*/
public class PublisherSubscriberReceive {
    //连接账号
    private String userName = "";
    //连接密码
    private String password = "";
    //连接地址
    private String brokerURL = "tcp://127.0.0.1:61616";
    //connection的工厂
    private ConnectionFactory factory;
    //连接对象
    private Connection connection;
    //一个操作会话
    private Session session;
    //目的地，其实就是连接到哪个队列，如果是点对点，那么它的实现是Queue，如果是订阅模式，那它的实现是Topic
    private Destination destination;
    //生产者，就是产生数据的对象
    private MessageConsumer consumer;

    public void start(){
        try {
            //根据用户名，密码，url创建一个连接工厂
            factory = new ActiveMQConnectionFactory(userName, password, brokerURL);
            //从工厂中获取一个连接
            connection = factory.createConnection();
            //测试过这个步骤不写也是可以的，但是网上的各个文档都写了
            connection.start();
            //创建一个session
            //第一个参数:是否支持事务，如果为true，则会忽略第二个参数，被jms服务器设置为SESSION_TRANSACTED
            //第二个参数为false时，paramB的值可为Session.AUTO_ACKNOWLEDGE，Session.CLIENT_ACKNOWLEDGE，DUPS_OK_ACKNOWLEDGE其中一个。
            //Session.AUTO_ACKNOWLEDGE为自动确认，客户端发送和接收消息不需要做额外的工作。哪怕是接收端发生异常，也会被当作正常发送成功。
            //Session.CLIENT_ACKNOWLEDGE为客户端确认。客户端接收到消息后，必须调用javax.jms.Message的acknowledge方法。jms服务器才会当作发送成功，并删除消息。
            //DUPS_OK_ACKNOWLEDGE允许副本的确认模式。一旦接收方应用程序的方法调用从处理消息处返回，会话对象就会确认消息的接收；而且允许重复确认。
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //创建一个到达的目的地，其实想一下就知道了，activemq不可能同时只能跑一个队列吧，这里就是连接了一个名为"text-msg"的队列，这个会话将会到这个队列，当然，如果这个队列不存在，将会被创建



            //=======================================================
            //点对点与订阅模式唯一不同的地方，就是这一行代码，点对点创建的是Queue，而订阅模式创建的是Topic
            destination = session.createTopic("topic-text");
            //=======================================================



            //从session中，获取一个消息消费者
            consumer = session.createConsumer(destination);

            /*
            异步方式实现消息接收
             */
            //实现一个消息的监听器
            //实现这个监听器后，以后只要有消息，就会通过这个监听器接收到
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        //获取到接收的数据
                        String text = ((TextMessage)message).getText();
                        System.out.println(text);



                        //确认接收，并成功处理了消息
                        //message.acknowledge();

                        //上面接收端设置为CLIENT_ACKNOWLEDGE
                        //如果接收端不确认消息，那么activemq将会把这条消息一直保留，直到有一个接收端确定了消息。
                        //注意：只在点对点中有效，订阅模式，即使不确认，也不会保存消息
                        //session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);




                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });

            //关闭接收端，也不会终止程序哦
            //consumer.close();


            //同步方式实现消息接收
           /* Message message = consumer.receive();
            while (message != null) {
                TextMessage txtMsg = (TextMessage) message;
                System.out.println("收到消息：" + txtMsg.getText());
                message = consumer.receive(1000L);
            }
            //关闭接收端，也不会终止程序哦
            //consumer.close();
            */
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PublisherSubscriberReceive receive = new PublisherSubscriberReceive();
        receive.start();
    }
}
