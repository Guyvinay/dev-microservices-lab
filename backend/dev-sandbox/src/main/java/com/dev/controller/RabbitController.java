package com.dev.controller;

import com.dev.common.dto.Profile;
import com.dev.common.dto.document.*;
import com.dev.rmq.service.RabbitVirtualHosts;
import com.dev.rmq.utility.Queues;
import com.dev.rmq.wrapper.RabbitTemplateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping(value = "rabbit")
@Slf4j
public class RabbitController {
//    @Autowired
    private RabbitTemplateWrapper rmqWrapper;

//    @Autowired
    private RabbitVirtualHosts rmqService;

    @GetMapping
    public String send(@RequestParam String queue) {
        String messageStr;
        Profile profile = new Profile(UUID.randomUUID().toString(), "Vinay Kumar Singh", "vinay@gmail.com", 23);
        Document document = createDocument();
        try {
            messageStr = new ObjectMapper().writeValueAsString(document);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if(queue.equalsIgnoreCase(Queues.QUEUE1)) {
            rmqWrapper.convertAndSend(Queues.QUEUE1, messageStr);
        }else if(queue.equalsIgnoreCase(Queues.QUEUE2)) {
            rmqWrapper.convertAndSend(Queues.QUEUE1, messageStr);
        }else if(queue.equalsIgnoreCase(Queues.QUEUE3)) {
            rmqWrapper.convertAndSend(Queues.QUEUE3, messageStr);
        }else if(queue.equalsIgnoreCase(Queues.QUEUE4)) {
            rmqWrapper.convertAndSend(Queues.QUEUE4, messageStr);
        } else {
            return "pushing in undeclared queue";
        }
        return "Message Sent Successfully";
    }

    private Document createDocument() {
        Location location = new Location();
        location.setLat(40.7128);
        location.setLon(-74.0060);

        Address address = new Address();
        address.setStreet("123 Main St");
        address.setCity("Metropolis");
        address.setState("NY");
        address.setZip(10001);
        address.setLocation(location);

        User user = new User();
        user.setId("u123");
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setAddress(address);
        user.setAge(30);
        user.setDateOfBirth("1993-01-01");
        user.setCreatedAt("2023-06-21T14:22:10Z");

        Comment comment = new Comment();
        comment.setCommentId("c123");
        comment.setUserId("u456");
        comment.setComment("Great post!");
        comment.setCreatedAt("2023-06-21T15:22:10Z");

        Post post = new Post();
        post.setPostId("p123");
        post.setTitle("My First Post");
        post.setContent("This is the content of my first post.");
        post.setTags(Collections.singletonList("intro"));
        post.setComments(Collections.singletonList(comment));
        post.setCreatedAt("2023-06-21T14:30:10Z");

        Notifications notifications = new Notifications();
        notifications.setEmail(true);
        notifications.setSms(false);
        notifications.setPush(true);


        Privacy privacy = new Privacy();
        privacy.setProfileVisibility("public");
        privacy.setSearchVisibility("private");

        Settings settings = new Settings();
        settings.setTheme("dark");
        settings.setNotifications(notifications);
        settings.setPrivacy(privacy);

        Document document = new Document();
        document.setUser(user);
        document.setPosts(Collections.singletonList(post));
        document.setSettings(settings);

        return document;
    }


    @GetMapping(value = "/createVHost/{vHost}")
    public void createVhost(@PathVariable String vHost){
        rmqService.createVirtualHostAndQueues(vHost);
    }



}
