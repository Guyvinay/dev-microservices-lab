package com.pros.controller;

import com.pros.rmq.service.RmqService;
import com.pros.utils.QueueListeners;
import com.pros.wrapper.RmqWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        rmqWrapper.convertAndSend(QueueListeners.QUEUE2, message);
        return "Message Sent Successfully";
    }


    @GetMapping(value = "/createVHost/{vHost}")
    public void createVhost(@PathVariable String vHost){
        rmqService.createVirtualHostAndQueues(vHost);
    }



}
