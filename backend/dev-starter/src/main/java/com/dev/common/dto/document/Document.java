package com.dev.common.dto.document;

import lombok.Data;

import java.util.List;

@Data
public class Document {
    private User user;
    private List<Post> posts;
    private Settings settings;
}
