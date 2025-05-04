package com.sonnvt.blog.database.jpa;

import com.sonnvt.blog.database.entity.User;
import com.sonnvt.blog.database.projection.UserProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserJpa extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    @Query(value = """
        SELECT\s
            u.id,\s
            u.username,\s
            u.password,
            u.email,\s
            u.avatar,\s
            u.first_name as firstName,\s
            u.last_name as lastName,\s
            (SELECT COUNT(*) FROM public.followers WHERE id_following = u.id) AS followers,
            (SELECT COUNT(*) FROM public.followers WHERE id_user = u.id) AS following,
            u.quotes,\s
            u.created_at as createdAt,\s
            u.updated_at as updatedAt,\s
            STRING_AGG(r.name, ', ') AS roles\s
        FROM users u\s
        JOIN public.user_roles ur ON u.id = ur.user_id\s
        JOIN public.roles r ON ur.role_id = r.id\s
        WHERE u.email = :email\s
        GROUP BY u.id, u.username, u.email, u.avatar, u.first_name, u.last_name, u.quotes, u.created_at, u.updated_at
   \s""", nativeQuery = true)
    UserProjection findByEmail(@Param("email") String email);

    @Query(value = """
        SELECT\s
            u.id,\s
            u.username,\s
            u.password,
            u.email,\s
            u.avatar,\s
            u.first_name as firstName,\s
            u.last_name as lastName,\s
            (SELECT COUNT(*) FROM public.followers WHERE id_following = u.id) AS followers,
            (SELECT COUNT(*) FROM public.followers WHERE id_user = u.id) AS following,
            u.quotes,\s
            u.created_at as createdAt,\s
            u.updated_at as updatedAt,\s
            STRING_AGG(r.name, ', ') AS roles\s
        FROM users u\s
        JOIN public.user_roles ur ON u.id = ur.user_id\s
        JOIN public.roles r ON ur.role_id = r.id\s
        WHERE u.username = :username OR u.email = :email
        GROUP BY u.id, u.username, u.email, u.avatar, u.first_name, u.last_name, u.quotes, u.created_at, u.updated_at
   \s""", nativeQuery = true)
    UserProjection findByUsernameOrEmail(@Param("username") String username, @Param("email") String email);

    @Query(value = """
        SELECT\s
            u.id,\s
            u.username,\s
            u.password,
            u.email,\s
            u.avatar,\s
            u.first_name as firstName,\s
            u.last_name as lastName,\s
            (SELECT COUNT(*) FROM public.followers WHERE id_following = u.id) AS followers,
            (SELECT COUNT(*) FROM public.followers WHERE id_user = u.id) AS following,
            u.quotes,\s
            u.created_at as createdAt,\s
            u.updated_at as updatedAt,\s
            STRING_AGG(r.name, ', ') AS roles\s
        FROM users u\s
        JOIN public.user_roles ur ON u.id = ur.user_id\s
        JOIN public.roles r ON ur.role_id = r.id\s
        WHERE u.id = :id\s
        GROUP BY u.id, u.username, u.email, u.avatar, u.first_name, u.last_name, u.quotes, u.created_at, u.updated_at
   \s""", nativeQuery = true)
    UserProjection findByID(@Param("id") Long id);

    @Query(value = """
        SELECT\s
            u.id,\s
            u.username,\s
            u.password,
            u.email,\s
            u.avatar,\s
            u.first_name as firstName,\s
            u.last_name as lastName,\s
            (SELECT COUNT(*) FROM public.followers WHERE id_following = u.id) AS followers,
            (SELECT COUNT(*) FROM public.followers WHERE id_user = u.id) AS following,
            u.quotes,\s
            u.created_at as createdAt,\s
            u.updated_at as updatedAt,\s
            STRING_AGG(r.name, ', ') AS roles
        FROM users u\s
        JOIN public.user_roles ur ON u.id = ur.user_id\s
        JOIN public.roles r ON ur.role_id = r.id\s
        WHERE u.email ILIKE %:query%\s
           OR u.first_name ILIKE %:query%\s
           OR u.last_name ILIKE %:query%\s
        GROUP BY u.id, u.username, u.email, u.avatar, u.first_name, u.last_name, u.quotes, u.created_at, u.updated_at\s
        ORDER BY u.id ASC\s
   \s""", nativeQuery = true)
    Page<UserProjection> findByQuery(@Param("query") String query, Pageable pageable);

    @Query(value = "SELECT id FROM users", nativeQuery = true)
    List<Long> findAllIds();

    @Query(value = """
                SELECT
                    u.id,
                    u.username,
                    u.password,
                    u.email,
                    u.avatar,
                    u.first_name AS firstName,
                    u.last_name AS lastName,
                    (SELECT COUNT(*) FROM public.followers WHERE id_following = u.id) AS followers,
                    (SELECT COUNT(*) FROM public.followers WHERE id_user = u.id) AS following,
                    u.quotes,
                    u.created_at AS createdAt,
                    u.updated_at AS updatedAt,
                    STRING_AGG(r.name, ', ') AS roles
                FROM users u
                         JOIN public.user_roles ur ON u.id = ur.user_id
                         JOIN public.roles r ON ur.role_id = r.id
                         JOIN public.followers fl ON u.id = fl.id_user
                WHERE fl.id_following = :id
                GROUP BY u.id, u.username, u.email, u.avatar, u.first_name, u.last_name, u.quotes, u.created_at, u.updated_at\s
                LIMIT :limit OFFSET :offset
            \s""", nativeQuery = true)
    List<UserProjection> findAllFollower(@Param("id") Long id, @Param("limit") int limit, @Param("offset") int offset);

    @Query(value = """
                SELECT
                    u.id,
                    u.username,
                    u.password,
                    u.email,
                    u.avatar,
                    u.first_name AS firstName,
                    u.last_name AS lastName,
                    (SELECT COUNT(*) FROM public.followers WHERE id_following = u.id) AS followers,
                    (SELECT COUNT(*) FROM public.followers WHERE id_user = u.id) AS following,
                    u.quotes,
                    u.created_at AS createdAt,
                    u.updated_at AS updatedAt,
                    STRING_AGG(r.name, ', ') AS roles
                FROM users u
                         JOIN public.user_roles ur ON u.id = ur.user_id
                         JOIN public.roles r ON ur.role_id = r.id
                         JOIN public.followers fl ON u.id = fl.id_following
                WHERE fl.id_user = :id
                GROUP BY u.id, u.username, u.email, u.avatar, u.first_name, u.last_name, u.quotes, u.created_at, u.updated_at\s
                LIMIT :limit OFFSET :offset
            \s""", nativeQuery = true)
    List<UserProjection> findAllFollowing(@Param("id") Long id, @Param("limit") int limit, @Param("offset") int offset);
}
