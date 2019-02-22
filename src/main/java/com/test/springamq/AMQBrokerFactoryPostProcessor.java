package com.test.springamq;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

public class AMQBrokerFactoryPostProcessor implements BeanFactoryPostProcessor {

//    private  String amqBrokerUrl;
//
//    public AMQBrokerFactoryPostProcessor(String AMQBrokerUrl) {
//        this.amqBrokerUrl = AMQBrokerUrl;
//    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        System.out.println("******调用了BeanFactoryPostProcessor");
        String[] beanStr = configurableListableBeanFactory.getBeanDefinitionNames();

        for (String beanName : beanStr) {
//            if ("amqBrokerBean".equals(beanName)) {
            if ("activeMQConnectionFactory".equals(beanName)) {
                    BeanDefinition beanDefinition = configurableListableBeanFactory.getBeanDefinition(beanName);
                MutablePropertyValues m = beanDefinition.getPropertyValues();
                if (m.contains("brokerURL")) {
                    // 我们自己去读取指定的配置文件
                    Properties prop = null;
                    try {
                        prop = PropertiesLoaderUtils.loadAllProperties("activemq.properties", Thread.currentThread().getContextClassLoader());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (null == prop) {
                        return;
                    }
                    String brokerurl = prop.getProperty("amq.brokerurl");

                    m.addPropertyValue("brokerURL", brokerurl);

                    System.out.println("》》》修改了brokerurl属性初始值了");
                }
            }
        }
    }
}
