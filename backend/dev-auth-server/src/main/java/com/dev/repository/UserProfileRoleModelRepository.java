package com.dev.repository;
import com.dev.entity.UserProfileRoleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for performing CRUD operations on UserProfileRoleInfoModel.
 * This interface extends JpaRepository to provide standard methods for interacting with the database.
 */

@Repository
public interface UserProfileRoleModelRepository extends JpaRepository<UserProfileRoleModel, Long> {

    /**
     * Finds a list of UserProfileRoleInfoModel by the role ID.
     * @param roleId - The role ID.
     * @return List of UserProfileRoleInfoModel.
     */
    List<UserProfileRoleModel> findByRoleId(Long roleId);

    // Find roles by tenant
    List<UserProfileRoleModel> findByTenantId(String tenantId);

    // Check if role exists by name and tenant
    boolean existsByRoleNameAndTenantId(String roleName, String tenantId);
}