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

import javax.print.Doc;
import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping(value = "rabbit")
@Slf4j
public class RabbitController {
    @Autowired
    private RabbitTemplateWrapper rmqWrapper;

    @Autowired
    private RabbitVirtualHosts rmqService;

    @GetMapping
    public String send(@RequestParam String queue) {
        String messageStr = null;
//        Profile profile = new Profile(UUID.randomUUID().toString(), "Vinay Kumar Singh", "vinay@gmail.com", 23);
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
        user.setDate_of_birth("1993-01-01");
        user.setCreated_at("2023-06-21T14:22:10Z");

        Comment comment = new Comment();
        comment.setComment_id("c123");
        comment.setUser_id("u456");
        comment.setComment("Great post!");
        comment.setCreated_at("2023-06-21T15:22:10Z");

        Post post = new Post();
        post.setPost_id("p123");
        post.setTitle("My First Post");
        post.setContent("This is the content of my first post.");
        post.setTags(Collections.singletonList("intro"));
        post.setComments(Collections.singletonList(comment));
        post.setCreated_at("2023-06-21T14:30:10Z");

        Notifications notifications = new Notifications();
        notifications.setEmail(true);
        notifications.setSms(false);
        notifications.setPush(true);


        Privacy privacy = new Privacy();
        privacy.setProfile_visibility("public");
        privacy.setSearch_visibility("private");

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
