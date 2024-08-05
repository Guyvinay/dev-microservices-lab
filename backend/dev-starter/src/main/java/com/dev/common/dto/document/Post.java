package com.dev.common.dto.document;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Post {
    private String postId;
    private String title;
    private String content;
    private List<String> tags;
    private List<Comment> comments;
    private String createdAt;

}
