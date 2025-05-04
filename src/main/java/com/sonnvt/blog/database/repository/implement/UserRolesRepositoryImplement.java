package com.sonnvt.blog.database.repository.implement;

import com.sonnvt.blog.database.entity.UserRoles;
import com.sonnvt.blog.database.jpa.UserRolesJpa;
import com.sonnvt.blog.database.repository.UserRolesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRolesRepositoryImplement implements UserRolesRepository {
    private final UserRolesJpa userRolesJpa;

    @Override
    @Transactional
    public void register(Long userId) {
        userRolesJpa.save(UserRoles.builder()
                        .userId(userId)
                        .roleId(3).build());

        userRolesJpa.save(UserRoles.builder()
                        .userId(userId)
                        .roleId(4).build());

        userRolesJpa.save(UserRoles.builder()
                        .userId(userId)
                        .roleId(5).build());
    }
}
