package com.dev.common.dto.document;

import lombok.Data;

import java.util.List;

@Data
public class Post {
    private String post_id;
    private String title;
    private String content;
    private List<String> tags;
    private List<Comment> comments;
    private String created_at;

}
