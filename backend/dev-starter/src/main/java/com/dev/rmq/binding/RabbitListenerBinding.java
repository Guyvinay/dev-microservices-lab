package com.dev.rmq.binding;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
public class RabbitListenerBinding {


    @PostConstruct
    public void findRabbitListenerBeans() {

    }

}
