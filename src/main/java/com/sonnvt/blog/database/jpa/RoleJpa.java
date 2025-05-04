package com.sonnvt.blog.database.jpa;

import com.sonnvt.blog.database.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleJpa extends JpaRepository<Role, Long> {
    @Query(value = """
        SELECT r.* FROM roles r
        JOIN user_roles ur ON r.id = ur.role_id
        WHERE ur.user_id = :userId
    """, nativeQuery = true)
    List<Role> findRolesByIdUser(@Param("userId") long userId);
}
