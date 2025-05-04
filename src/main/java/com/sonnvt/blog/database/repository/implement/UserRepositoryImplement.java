package com.sonnvt.blog.database.repository.implement;

import com.sonnvt.blog.database.entity.Role;
import com.sonnvt.blog.database.entity.User;
import com.sonnvt.blog.database.entity.UserRoles;
import com.sonnvt.blog.database.jpa.RoleJpa;
import com.sonnvt.blog.database.jpa.UserJpa;
import com.sonnvt.blog.database.jpa.UserRolesJpa;
import com.sonnvt.blog.database.repository.UserRepository;
import com.sonnvt.blog.dto.UserInfoResponse;
import com.sonnvt.blog.database.projection.UserProjection;
import com.sonnvt.blog.exception.ex.UpdateDatabaseException;
import com.sonnvt.blog.utils.Utils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImplement implements UserRepository {
    private final UserJpa userJpa;
    private final RoleJpa roleJpa;
    private final UserRolesJpa userRolesJpa;

    @PersistenceContext
    protected EntityManager entityManager;

    @Override
    public Optional<UserInfoResponse> findById(Long id) {
        UserProjection user = userJpa.findByID(id);
        return mapUserInfoToUserInfoResponse(user);
    }

    @Override
    public Optional<UserInfoResponse> findByUsernameOrEmail(String username, String email) {
        UserProjection user = userJpa.findByUsernameOrEmail(username, email);
        return mapUserInfoToUserInfoResponse(user);
    }

    @Override
    public Optional<UserInfoResponse> findByEmail(String email) {
        UserProjection user = userJpa.findByEmail(email);
        return mapUserInfoToUserInfoResponse(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpa.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        return userJpa.save(user);
    }

    @Override
    public void update(Long id, String firstName, String lastName, String avatar) {
        StringBuilder query = new StringBuilder("UPDATE users ");
        Map<String, Object> params = new HashMap<>();
        if (!Utils.isEmpty(firstName)) {
            query.append("SET first_name = :firstName ");
            params.put("firstName", firstName);
        }
        if (!Utils.isEmpty(lastName)) {
            query.append("SET last_name = :lastName ");
            params.put("lastName", lastName);
        }
        if (!Utils.isEmpty(avatar)) {
            query.append("SET avatar = :avatar ");
            params.put("avatar", avatar);
        }
        query.append("WHERE id = :id");
        params.put("id", id);

        Query q = entityManager.createNativeQuery(query.toString());
        params.forEach(q::setParameter);
        int rowEffected = q.executeUpdate();
        if (rowEffected == 0) {
            throw new UpdateDatabaseException("Update user failed, 0 row effected");
        }
    }

    @Override
    public Page<UserInfoResponse> findByQuery(String query, Pageable pageable) {
        Page<UserProjection> user = userJpa.findByQuery(query, pageable);
        return user.map(this::mapUserInfoToUserInfoResponse).map(Optional::get);
    }

    @Override
    public List<Long> findAllIds() {
        return userJpa.findAllIds();
    }

    @Override
    public List<UserInfoResponse> findAllFollowers(Long id, int limit, int offset) {
        List<UserProjection> users = userJpa.findAllFollower(id, limit, offset);
        return users.stream()
                .map(this::mapUserInfoToUserInfoResponse)
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    public List<UserInfoResponse> findAllFollowing(Long id, int limit, int offset) {
        List<UserProjection> users = userJpa.findAllFollowing(id, limit, offset);
        return users.stream()
                .map(this::mapUserInfoToUserInfoResponse)
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    @Transactional
    public void revertRole(Long idUser, Integer idRole) {
        List<Role> roles = roleJpa.findRolesByIdUser(idUser);
        List<Integer> roleIds = roles.stream().map(Role::getId).toList();
        if (roleIds.contains(idRole)) {
            userRolesJpa.deleteByUserIdAndRoleId(idUser, idRole);
        } else {
            userRolesJpa.save(UserRoles.builder()
                            .roleId(idRole)
                            .userId(idUser).build());
        }
    }

    private Optional<UserInfoResponse> mapUserInfoToUserInfoResponse(UserProjection user) {
        if (user == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .followers(user.getFollowers())
                .following(user.getFollowing())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .quotes(user.getQuotes())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build());
    }
}
