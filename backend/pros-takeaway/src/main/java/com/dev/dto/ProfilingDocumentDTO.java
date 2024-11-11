package com.dev.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO representing the profiling attributes.")
public class ProfilingDocumentDTO {



    @Schema(name = "column", description = "represents fields id and document id", example = "FLD_123456")
    private String column;
    private Long timestamp;
    private Long null_count;
    private Long non_null_count;
    private Double null_percentage;
    private Double non_null_percentage;
    private Long distinct_count;
    private Long non_distinct_count;
    private Double distinct_percentage;
    private Double non_distinct_percentage;
    private Long unique_count;
    private Long non_unique_count;
    private Double unique_percentage;
    private Double non_unique_percentage;
    private Integer shortest_length;
    private Integer longest_length;
    private Integer average_length;
    private String shortest_value;
    private String longest_value;
    private String first_sorted;
    private String last_sorted;
    private Integer leading_space_count;
    private Integer trailing_space_count;
    private Integer both_space_count;
    private Integer no_space_count;

    @Schema(name = "value_frequency", description = "represents the value frequency",
            example = "{'test':12, 'test 1':10}"
    )
    private Map<String, Long> value_frequency;
}
