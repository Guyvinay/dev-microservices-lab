package com.dev.controller;

import com.dev.rmq.utility.Queues;
import com.dev.rmq.wrapper.RabbitTemplateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Queue;

@RestController
@RequestMapping(value = "/rabbit")
public class RabbitController {

    @Autowired
    private RabbitTemplateWrapper rabbitTemplateWrapper;

    @GetMapping(value = "/{queue}")
    public String sendMessage(@PathVariable("queue")String queue) {
        String messageStr = "hello from rmq";
        if(queue.equalsIgnoreCase(Queues.QUEUE1)) {
            rabbitTemplateWrapper.convertAndSend(Queues.QUEUE1, messageStr);
        }else if(queue.equalsIgnoreCase(Queues.QUEUE2)) {
            rabbitTemplateWrapper.convertAndSend(Queues.QUEUE2, messageStr);
        }else if(queue.equalsIgnoreCase(Queues.QUEUE3)) {
            rabbitTemplateWrapper.convertAndSend(Queues.QUEUE3, messageStr);
        }
        return "Message sent";
    }
}
