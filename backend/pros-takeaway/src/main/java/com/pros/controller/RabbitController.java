package com.pros.controller;

import com.pros.rmq.service.RmqService;
import com.pros.service.RabbitProducer;
import com.pros.utils.VirtualHostCreation;
import com.pros.wrapper.RmqWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private VirtualHostCreation virtualHostCreation;

    @Autowired
    private RmqService rmqService;

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
    @GetMapping(value = "/createVHost/{vHost}")
    public void createVhost(@PathVariable String vHost){
        rmqService.createVirtualHostV2(vHost);
    }



}
