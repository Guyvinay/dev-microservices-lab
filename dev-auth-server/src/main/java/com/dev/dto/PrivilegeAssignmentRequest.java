package com.dev.dto;

import com.dev.entity.enums.Action;
import com.dev.entity.enums.Area;
import com.dev.entity.enums.Privilege;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PrivilegeAssignmentRequest {
    private Long roleId;
    private Privilege privilege;
    private Action action;
    private Area area;
    private Long assignedBy;
}

