package com.dev.library.webSocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionCountsDTO {
    private int like;
    private int love;
    private int laugh;
    private int sad;
    private int angry;
}
