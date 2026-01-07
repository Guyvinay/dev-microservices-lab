package com.dev.repository;


import com.dev.dto.privilege.Action;
import com.dev.dto.privilege.Area;
import com.dev.dto.privilege.Privilege;
import com.dev.entity.UserProfilePrivilegeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    boolean existsByRoleId(Long roleId);

//    @RedisCacheAdapter(log = true)
    @Query("""
        select count(p) > 0
        from UserProfilePrivilegeModel p
        where p.roleId in :activeRoleIds
          and p.privilege = :privilege
          and p.action in :actions
    """)
    boolean existsByRoleIdsAndPrivilegeAndActions(
            @Param("activeRoleIds") List<Long> activeRoleIds,
            @Param("privilege") Privilege privilege,
            @Param("actions") Set<Action> actions);
}
