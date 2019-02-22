package com.test;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Power by xidianzxm
 */
public class PTPSend
{
    static int size = 200000;
    private static String str = "send message";
    //连接账号
    private static String userName = "";

    //连接密码
    private static String password = "";

    //连接地址
    private static String brokerURL = "tcp://127.0.0.1:61616";

    //connection的工厂
    private static ConnectionFactory factory;

    //连接对象
    private static Connection connection;

    //一个操作会话
    private static Session session;

    //目的地，其实就是连接到哪个队列，如果是点对点，那么它的实现是Queue，如果是订阅模式，那它的实现是Topic
    private static Destination destination;

    //生产者，就是产生数据的对象
    private static MessageProducer producer;


    public static void init_connection() throws Exception {
        //根据用户名，密码，url创建一个连接工厂
        //factory = new ActiveMQConnectionFactory(userName, password, brokerURL);
        factory = new ActiveMQConnectionFactory(brokerURL);

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
        destination = session.createQueue("BOX_CODEINFO_TOCKI_QUEUE_GREY");

        //从session中，获取一个消息生产者
        producer = session.createProducer(destination);

        //设置生产者的模式，有两种可选
        //DeliveryMode.PERSISTENT 当activemq关闭的时候，队列数据将会被保存
        //DeliveryMode.NON_PERSISTENT 当activemq关闭的时候，队列里面的数据将会被清空
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);

    }
    public static void sendMessage(String msg) {
        TextMessage textMsg = null;
        try {
            textMsg = session.createTextMessage(msg);
            producer.send(textMsg);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
    public static void close() throws Exception {
        connection.close();
    }

    public void start(){
        try {


            //创建一条消息，当然，消息的类型有很多，如文字，字节，对象等,可以通过session.create..方法来创建出来
            String txtMsg = "test message";

            /*for(int i = 0 ; i < 5 ; i ++){
                //发送一条消息
                producer.send(textMsg);
            }*/

           // System.out.println("发送消息成功");

            //即便生产者的对象关闭了，程序还在运行哦;但在amq管理端看的话，生产者已经不是active状态了
            producer.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

        public static void main( String[] args )
    {

        long start = System.currentTimeMillis();
        ExecutorService es = Executors.newFixedThreadPool(2000);
        final CountDownLatch cdl = new CountDownLatch(size);
        try{
            init_connection();

            for (int a = 0; a < size; a++) {
                es.execute(
                        new Runnable() {
                            @Override
                            public void run() {
                                sendMessage(str);
                                cdl.countDown();
                            }
                        }
                );
            }

            cdl.await();

        }catch (Exception e){
            e.printStackTrace();
        }

            es.shutdown();

        long time = System.currentTimeMillis() - start;
        System.out.println("插入" + size + "条JSON，共消耗：" + (double)time / 1000 + " s");
        System.out.println("平均：" + size / ((double)time/1000) + " 条/秒");





        //PTPSend send = new PTPSend();
        //send.start();
    }
}
