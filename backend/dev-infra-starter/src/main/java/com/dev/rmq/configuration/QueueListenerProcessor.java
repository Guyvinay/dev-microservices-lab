package com.dev.rmq.configuration;

import com.dev.rmq.annotation.RabbitListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import org.springframework.core.annotation.AnnotationUtils;

//Not in use

//@Configuration
@Slf4j
public class QueueListenerProcessor implements BeanPostProcessor {

    @Autowired
    private ConnectionFactory connectionFactory;


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        log.info("QueueListener Processor");
        RabbitListener queueListener = AnnotationUtils.findAnnotation(bean.getClass(), RabbitListener.class);
        if(queueListener != null) {
            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
            container.setConnectionFactory(connectionFactory);
            container.setQueueNames(queueListener.queue());
            container.setMessageListener(new MessageListenerAdapter(bean));
//            container.setPrefetchCount(queueListener.prefetchCount());
            container.start();
        }
        return bean;
    }
}
