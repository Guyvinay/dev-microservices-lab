package com.pros.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskDto {
    private String status;
    private String assignee;
    private LocalDateTime createdAt;
    private String description;
    private String priority;
}
