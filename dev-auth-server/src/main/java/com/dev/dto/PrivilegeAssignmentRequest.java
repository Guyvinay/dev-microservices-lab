package com.dev.dto;

import com.dev.dto.privilege.Action;
import com.dev.dto.privilege.Area;
import com.dev.dto.privilege.Privilege;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PrivilegeAssignmentRequest {
    private Long roleId;
    private Privilege privilege;
    private Action action;
    private Area area;
    private UUID assignedBy;
}

