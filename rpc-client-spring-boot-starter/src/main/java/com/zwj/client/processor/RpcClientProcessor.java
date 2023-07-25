package com.zwj.client.processor;

import com.zwj.client.annotation.RpcAutowired;
import com.zwj.client.config.RpcClientProperties;
import com.zwj.client.proxy.ClientStubProxyFactory;
import com.zwj.core.discovery.DiscoveryService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * @Classname RpcClientProcessor
 * @Description bean 后置处理器 获取所有bean
 * 判断bean字段是否被 {@link com.zwj.client.annotation.RpcAutowired } 注解修饰
 * 动态修改被修饰字段的值为代理对象 {@link ClientStubProxyFactory}
 * @Date 2021/7/5 16:06
 * @Created by wangchangjiu
 */
public class RpcClientProcessor implements BeanFactoryPostProcessor, ApplicationContextAware {

    private ClientStubProxyFactory clientStubProxyFactory;

    private DiscoveryService discoveryService;

    private RpcClientProperties properties;

    private ApplicationContext applicationContext;

    public RpcClientProcessor(ClientStubProxyFactory clientStubProxyFactory, DiscoveryService discoveryService, RpcClientProperties properties) {
        this.clientStubProxyFactory = clientStubProxyFactory;
        this.discoveryService = discoveryService;
        this.properties = properties;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
            //getBeanDefinition(beanDefinitionName)：这是 BeanFactory 接口的一个方法，用于根据指定的 Bean 名称获取对应的 Bean 定义信息
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            //获取该 Bean 的类名
            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName != null) {
                //根据给定的类名字符串 beanClassName 和当前类的类加载器，解析出对应的 Class 对象
                Class<?> clazz = ClassUtils.resolveClassName(beanClassName, this.getClass().getClassLoader());
                /*doWithFields方法接受两个参数：
                第一个参数是要处理的类（clazz）。
                第二个参数是一个FieldCallback接口的实现，用于定义对字段的具体处理操作。*/
                ReflectionUtils.doWithFields(clazz, field -> {
                    RpcAutowired rpcAutowired = AnnotationUtils.getAnnotation(field, RpcAutowired.class);
                    if (rpcAutowired != null) {
                        Object bean = applicationContext.getBean(clazz);
                        field.setAccessible(true);
                        // 修改为代理对象
                        ReflectionUtils.setField(field, bean, clientStubProxyFactory.getProxy(field.getType(), rpcAutowired.version(), discoveryService, properties));
                    }
                });
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
