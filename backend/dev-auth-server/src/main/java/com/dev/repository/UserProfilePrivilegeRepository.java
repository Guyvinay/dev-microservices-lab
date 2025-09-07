package com.dev.repository;


import com.dev.entity.UserProfilePrivilegeModel;
import com.dev.entity.enums.Action;
import com.dev.entity.enums.Area;
import com.dev.entity.enums.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for performing CRUD operations on UserProfilePrivilegeInfoModel.
 * This interface extends JpaRepository to provide standard methods for interacting with the database.
 */
@Repository
public interface UserProfilePrivilegeRepository extends JpaRepository<UserProfilePrivilegeModel, UUID> {

    /**
     * Finds a list of UserProfilePrivilegeInfoModel by the role ID.
     * @param roleId - The role ID.
     * @return List of UserProfilePrivilegeInfoModel.
     */
    List<UserProfilePrivilegeModel> findByRoleId(Long roleId);

    /**
     * Finds a list of UserProfilePrivilegeInfoModel by the privilege name.
     * @param privilege - The privilege name.
     * @return List of UserProfilePrivilegeInfoModel.
     */
    List<UserProfilePrivilegeModel> findByPrivilege(String privilege);

    // Check if a specific privilege-action-area already exists for a role
    Optional<UserProfilePrivilegeModel> findByRoleIdAndPrivilegeAndActionAndArea(
            Long roleId,
            Privilege privilege,
            Action action,
            Area area
    );

    // Delete a privilege assignment
    void deleteByRoleIdAndPrivilegeAndActionAndArea(
            Long roleId,
            Privilege privilege,
            Action action,
            Area area
    );
}
