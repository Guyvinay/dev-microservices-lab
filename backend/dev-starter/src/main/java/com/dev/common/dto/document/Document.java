package com.dev.common.dto.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.elasticsearch.client.ml.job.results.AnomalyCause;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Document {
    private User user;
    private List<Post> posts;
    private Settings settings;
}
