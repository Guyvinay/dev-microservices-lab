package com.pros.service;

public interface TaskService {
    public void searchTasks(String status, String assignee, String searchString, int from, int size, String searchAfter);
}
