package com.dev.common.dto.document;

import lombok.Data;

@Data
public class Comment {
    private String comment_id;
    private String user_id;
    private String comment;
    private String created_at;
}
