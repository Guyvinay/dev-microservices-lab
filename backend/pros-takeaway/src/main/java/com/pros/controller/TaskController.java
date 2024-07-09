package com.pros.controller;

import com.pros.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/search")
    public void searchTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String assignee,
            @RequestParam(required = false) String searchString,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchAfter) {
        taskService.searchTasks(status, assignee, searchString, from, size, searchAfter);
    }

}
