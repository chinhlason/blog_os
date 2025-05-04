package com.sonnvt.blog.database.repository;

import com.sonnvt.blog.database.entity.User;
import com.sonnvt.blog.dto.UserInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<UserInfoResponse> findById(Long id);
    Optional<UserInfoResponse> findByUsernameOrEmail(String username, String email);
    Optional<UserInfoResponse> findByEmail(String email);
    boolean existsByEmail(String email);
    User save(User user);
    void update(Long id, String firstName, String lastName, String avatar);
    Page<UserInfoResponse> findByQuery(String query, Pageable pageable);
    List<Long> findAllIds();
    List<UserInfoResponse> findAllFollowers(Long id, int limit, int offset);
    List<UserInfoResponse> findAllFollowing(Long id, int limit, int offset);
    void revertRole(Long idUser, Integer idRole);
}
