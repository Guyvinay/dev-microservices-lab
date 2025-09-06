package com.dev.common.dto.document;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Privacy {
    private String profileVisibility;
    private String searchVisibility;
}
