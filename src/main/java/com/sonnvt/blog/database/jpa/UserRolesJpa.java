package com.sonnvt.blog.database.jpa;

import com.sonnvt.blog.database.entity.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRolesJpa extends JpaRepository<UserRoles, Integer> {
    @Modifying
    @Query(value = """
            DELETE FROM user_roles
            WHERE user_id = :userId AND role_id = :roleId
        """, nativeQuery = true)
    void deleteByUserIdAndRoleId(Long userId, Integer roleId);
}
