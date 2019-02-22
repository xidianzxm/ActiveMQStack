package com.test.springamq;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) {
            System.out.println("》》》Spring ApplicationContext容器开始初始化了......");
            ApplicationContext applicationContext = null;
            applicationContext = new ClassPathXmlApplicationContext(new String[] { "spring-amq.xml"});
            System.out.println("》》》Spring ApplicationContext容器初始化完毕了......");

//            AMQClient amqp2PClientClient = new AMQClient((ConfigurableApplicationContext) applicationContext);


            AMQP2PClient amqp2PClient = applicationContext.getBean(AMQP2PClient.class);
            AMQPSClient amqpsClient = applicationContext.getBean(AMQPSClient.class);


            User user = new User();
            user.setId(1);
            user.setName("name");
            user.setAge(1);

           // amqp2PClient.sendMsg(user);

            /*
            指定Topic name，虽然xml中制定了tpoic名字但在代码中可以重新进行指定
             */
           // amqpsClient.sendMsg("rename topicName",user);

            /*
            不指定Topic name，在xml中配置
             */
            amqpsClient.sendMsg(user);

            /*****************************************************************/
            System.out.println("complete");
    }
}
