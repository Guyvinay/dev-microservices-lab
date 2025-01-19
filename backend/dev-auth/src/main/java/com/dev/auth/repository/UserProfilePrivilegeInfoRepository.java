package com.dev.auth.repository;


import com.dev.auth.entity.UserProfilePrivilegeInfoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for performing CRUD operations on UserProfilePrivilegeInfoModel.
 * This interface extends JpaRepository to provide standard methods for interacting with the database.
 */
@Repository
public interface UserProfilePrivilegeInfoRepository extends JpaRepository<UserProfilePrivilegeInfoModel, UUID> {

    /**
     * Finds a list of UserProfilePrivilegeInfoModel by the role ID.
     * @param roleId - The role ID.
     * @return List of UserProfilePrivilegeInfoModel.
     */
    List<UserProfilePrivilegeInfoModel> findByRoleId(Long roleId);

    /**
     * Finds a list of UserProfilePrivilegeInfoModel by the privilege name.
     * @param privilege - The privilege name.
     * @return List of UserProfilePrivilegeInfoModel.
     */
    List<UserProfilePrivilegeInfoModel> findByPrivilege(String privilege);
}
