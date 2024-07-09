package com.dev.service;

import lombok.extern.slf4j.Slf4j;

//@Component
@Slf4j
public class RabbitConsumer {


//				@RabbitListener(queues = "hello")
				public void receive(String message){
								log.info("Rabbit Consumer Consuming message:- "+message);
								System.out.println("Received Message:-"+message);
				}

//				@RabbitListener(queues = "myQueue")
//				public void receiveMessage(RabbitMessage message) {
//								System.out.println("Message received from myQueue:");
//								System.out.println("Sender: " + message.getSender());
//								System.out.println("Receiver: " + message.getReceiver());
//								System.out.println("Content: " + message.getContent());
//				}
//
//				@RabbitListener(queues = "highPriorityQueue")
//				public void receiveHighPriorityMessage(RabbitMessage message) {
//								System.out.println("High priority message received from highPriorityQueue:");
//								System.out.println("Sender: " + message.getSender());
//								System.out.println("Receiver: " + message.getReceiver());
//								System.out.println("Content: " + message.getContent());
//				}



}
