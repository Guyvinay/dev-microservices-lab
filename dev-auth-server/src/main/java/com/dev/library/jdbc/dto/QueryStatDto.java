package com.dev.library.jdbc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryStatDto {
    private long queryid;
    private String query;
    private Long calls;
    private Double totalExecTime;
    private Double meanExecTime;
    private Long rows;
    private Long sharedBlksRead;
    private Long sharedBlksHit;
    private Double blkReadTime;
    private Double blkWriteTime;
}
