package com.pros.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pros.modal.Profile;
import com.pros.rmq.service.RmqService;
import com.pros.utils.QueueListeners;
import com.pros.wrapper.RmqWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping(value = "rabbit")
@Slf4j
public class RabbitController {

    @Autowired
    private RmqWrapper rmqWrapper;

    @Autowired
    private RmqService rmqService;

    @GetMapping
    public String send(@RequestParam String message) {
        String messageStr = null;
        Profile profile = new Profile(UUID.randomUUID().toString(), "Vinay Kumar Singh", "vinay@gmail.com", 23);
        try {
            messageStr = new ObjectMapper().writeValueAsString(profile);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        rmqWrapper.convertAndSend(QueueListeners.QUEUE1, messageStr);
        return "Message Sent Successfully";
    }


    @GetMapping(value = "/createVHost/{vHost}")
    public void createVhost(@PathVariable String vHost){
        rmqService.createVirtualHostAndQueues(vHost);
    }



}
