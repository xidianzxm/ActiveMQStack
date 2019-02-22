package com.test.springamq.noconfig;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;

public class AMQBrokerBean implements InitializingBean,DisposableBean,BeanNameAware,BeanFactoryAware {
    private String brokerurl;

    public String getBrokerurl() {
        return brokerurl;
    }

    public void setBrokerurl(String brokerurl) {
        this.brokerurl = brokerurl;
    }


    @Override
    public void setBeanFactory(BeanFactory paramBeanFactory) throws BeansException {
//        System.out.println("》》》调用了BeanFactoryAware的setBeanFactory方法了");
    }

    @Override
    public void setBeanName(String paramString) {
//        System.out.println("》》》调用了BeanNameAware的setBeanName方法了");
    }

    @Override
    public void destroy() throws Exception {
//        System.out.println("》》》调用了DisposableBean的destroy方法了");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        System.out.println("》》》调用了Initailization的afterPropertiesSet方法了");
    }


}