package com.dev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("column")
    private String column;

    @JsonProperty("timestamp")
    private Long timestamp;

    @JsonProperty("null_count")
    private Long nullCount;

    @JsonProperty("non_null_count")
    private Long nonNullCount;

    @JsonProperty("null_percentage")
    private Double nullPercentage;

    @JsonProperty("non_null_percentage")
    private Double nonNullPercentage;

    @JsonProperty("distinct_count")
    private Long distinctCount;

    @JsonProperty("non_distinct_count")
    private Long nonDistinctCount;

    @JsonProperty("distinct_percentage")
    private Double distinctPercentage;

    @JsonProperty("non_distinct_percentage")
    private Double nonDistinctPercentage;

    @JsonProperty("unique_count")
    private Long uniqueCount;

    @JsonProperty("non_unique_count")
    private Long nonUniqueCount;

    @JsonProperty("unique_percentage")
    private Double uniquePercentage;

    @JsonProperty("non_unique_percentage")
    private Double nonUniquePercentage;

    @JsonProperty("shortest_length")
    private Integer shortestLength;

    @JsonProperty("longest_length")
    private Integer longestLength;

    @JsonProperty("average_length")
    private Integer averageLength;

    @JsonProperty("shortest_value")
    private String shortestValue;

    @JsonProperty("longest_value")
    private String longestValue;

    @JsonProperty("first_sorted")
    private String firstSorted;

    @JsonProperty("last_sorted")
    private String lastSorted;

    @JsonProperty("leading_space_count")
    private Integer leadingSpaceCount;

    @JsonProperty("trailing_space_count")
    private Integer trailingSpaceCount;

    @JsonProperty("both_space_count")
    private Integer bothSpaceCount;

    @JsonProperty("no_space_count")
    private Integer noSpaceCount;

    @Schema(name = "value_frequency", description = "represents the value frequency",
            example = "{'test':12, 'test 1':10}"
    )
    @JsonProperty("value_frequency")
    private Map<String, Long> valueFrequency;

}
