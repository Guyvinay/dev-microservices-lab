package com.dev.rmq.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

import java.util.Arrays;

@Slf4j
public class ApplicationStartEventListener implements ApplicationListener<ApplicationStartedEvent> {
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.info("MS is up");
        ApplicationContext applicationContext = event.getApplicationContext();
        String[] beans = applicationContext.getBeanNamesForType(SimpleMessageListenerContainer.class);
        for (String string : Arrays.asList(beans)) {
            System.out.println(string);
        }

    }
}
