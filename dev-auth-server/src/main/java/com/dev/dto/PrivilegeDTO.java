package com.dev.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PrivilegeDTO {
    private String name;
    private String description;
    private String area;
    private List<ActionDTO> actions;
}
