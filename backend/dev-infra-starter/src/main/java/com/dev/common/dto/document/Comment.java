package com.dev.common.dto.document;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Comment {
    private String commentId;
    private String userId;
    private String comment;
    private String createdAt;
}
