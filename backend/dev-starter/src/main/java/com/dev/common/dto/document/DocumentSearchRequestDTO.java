package com.dev.common.dto.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentSearchRequestDTO {

    private String userId;
    private String userName;
    private String userEmail;
    private int userAge;
    private String userDateOfBirth;
    private List<String> postTags;
    private String profileTheme;
    private String profileVisibility;
    private String searchVisibility;
}
