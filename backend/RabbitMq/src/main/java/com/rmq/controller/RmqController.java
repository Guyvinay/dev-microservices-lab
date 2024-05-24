package com.rmq.controller;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

@RestController
@RequestMapping(value = "/api",
                produces = MediaType.APPLICATION_JSON_VALUE,
                consumes = MediaType.APPLICATION_JSON_VALUE)
public class RmqController {
				private RabbitTemplate rabbitTemplate;

				public RmqController(RabbitTemplate rabbitTemplate){
								this.rabbitTemplate=rabbitTemplate;
				}

				public void

}
