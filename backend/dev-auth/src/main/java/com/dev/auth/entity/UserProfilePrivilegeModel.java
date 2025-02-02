package com.dev.auth.entity;

import com.dev.auth.entity.enums.Area;
import com.dev.auth.entity.enums.Privilege;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "USER_PROFILE_PRIVILEGE_INFO_MODEL")
@ApiModel(description = "Represents user privileges assigned to a specific role in the system.")
public class UserProfilePrivilegeModel {

    @Id
    @GeneratedValue
    @Column(name = "ID", updatable = false, nullable = false)
    @ApiModelProperty(name = "id", value = "Unique identifier of the user privilege record", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Column(name = "ROLE_ID", nullable = false)
    @ApiModelProperty(name = "roleId", value = "Role ID associated with this privilege", example = "101")
    private Long roleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "PRIVILEGE", nullable = false)
    @ApiModelProperty(name = "privilege", value = "Specific privilege assigned", example = "Manage_Users")
    private Privilege privilege;

    @Column(name = "ACTION", nullable = false, length = 50)
    @ApiModelProperty(name = "action", value = "Type of action allowed with this privilege", example = "MaintainSchema_Create")
    private String action;

    @Enumerated(EnumType.STRING)
    @Column(name = "AREA", nullable = false)
    @ApiModelProperty(name = "area", value = "System area where this privilege applies", example = "ADMIN_PANEL")
    private Area area;

    @Column(name = "ASSIGNED_AT", nullable = false, updatable = false)
    @ApiModelProperty(name = "assignedAt", value = "Timestamp when the privilege was assigned", example = "1705741200000")
    private Long assignedAt;

    @Column(name = "ASSIGNED_BY", nullable = false)
    @ApiModelProperty(name = "assignedBy", value = "User ID who assigned this privilege", example = "1001")
    private Long assignedBy;

    public UserProfilePrivilegeModel() {
        this.assignedAt = Instant.now().toEpochMilli();
    }
}


/**
 {
 "id": "550e8400-e29b-41d4-a716-446655440000",
 "roleId": 101,
 "privilege": "Manage_Users",
 "action": "MaintainSchema_Create",
 "area": "ADMIN_PANEL",
 "assignedAt": 1705741200000,
 "assignedBy": 1001
 }
 */