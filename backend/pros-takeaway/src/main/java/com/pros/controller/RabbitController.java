package com.pros.controller;

import com.pros.service.RabbitProducer;
import com.pros.wrapper.RmqWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "rabbit")
@Slf4j
public class RabbitController {

//    @Autowired
//    private RabbitProducer rabbitProducer;

//    @Autowired
//    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RmqWrapper rmqWrapper;

//    @GetMapping
//    public String send(@RequestParam String message) {
//        rabbitProducer.sendMessage(message);
//        return "Message Sent Successfully";
//    }

    @GetMapping
    public String send(@RequestParam String message) {
        rmqWrapper.convertAndSend("1234", message);
        return "Message Sent Successfully";
    }


}
