package com.dev.repository;
import com.dev.entity.UserProfileRoleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

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

    Optional<UserProfileRoleModel> findByRoleNameAndTenantId(String roleName, String tenantId);

    // Check if role exists by name and tenant
    boolean existsByRoleNameAndTenantId(String roleName, String tenantId);

    @Query("""
        select r.roleId
        from UserProfileRoleModel r
        where r.tenantId = :tenantId
          and r.isActive = true
          and r.roleId in :roleIds
    """)
    List<Long> findActiveRoleIdsByTenantIdAndRoleIds(
            @Param("tenantId") String tenantId,
            @Param("roleIds") List<Long> roleIds
    );

//    @RedisCacheAdapter(log = true)
    List<UserProfileRoleModel> findByRoleIdInAndTenantIdAndIsActiveTrue(
            List<Long> roleIds,
            String tenantId
    );
}