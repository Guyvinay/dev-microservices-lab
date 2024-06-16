package com.pros.controller;

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
//        boolean isVhost = virtualHostCreation.checkVHost(vHost);
//        if(isVhost) {
//            System.out.println("Virtual Host Available");
//        }else {
//            virtualHostCreation.createVirtualHost(vHost);
//        }
        List<String> tags = new ArrayList<>();
        tags.add("tasg");
        virtualHostCreation.createVirtualHostV2(vHost);
    }



}
