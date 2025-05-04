package com.sonnvt.blog.service.implement;

import com.sonnvt.blog.database.dao.UserDashboardDao;
import com.sonnvt.blog.database.entity.Role;
import com.sonnvt.blog.database.jpa.RoleJpa;
import com.sonnvt.blog.database.repository.AdminRepository;
import com.sonnvt.blog.database.repository.UserRepository;
import com.sonnvt.blog.dto.UserDashboardResponse;
import com.sonnvt.blog.dto.UserInfoResponse;
import com.sonnvt.blog.enums.ERole;
import com.sonnvt.blog.exception.ex.BadRequestException;
import com.sonnvt.blog.exception.ex.SystemException;
import com.sonnvt.blog.security.JwtUtils;
import com.sonnvt.blog.service.AdminService;
import com.sonnvt.blog.cache.TokenCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImplement implements AdminService {
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final RoleJpa roleJpa;
    private final JwtUtils jwtUtils;
    private final TokenCache customCache;

    @Override
    public UserDashboardResponse getUsers(Pageable pageable) {
        UserDashboardDao userDashboardDao = adminRepository.getUsers(pageable);
        UserDashboardResponse result = new UserDashboardResponse();
        List<UserDashboardResponse.UserData> listUserData = new ArrayList<>();
        userDashboardDao.getData().forEach(userDataDao -> {
            UserDashboardResponse.UserData data = new UserDashboardResponse.UserData();
            data.setId(userDataDao.getId());
            data.setFirstName(userDataDao.getFirstName());
            data.setLastName(userDataDao.getLastName());
            data.setEmail(userDataDao.getEmail());
            data.setRoles(convertToMap(userDataDao.getRoles()));
            data.setCreatedAt(userDataDao.getCreatedAt());
            listUserData.add(data);
        });
        result.setUsers(listUserData);
        result.setTotalRecords(userDashboardDao.getTotalRecords());
        return result;
    }

    @Override
    public void revertRole(Long idUser, Integer idRole) {
        UserInfoResponse u = userRepository.findById(idUser).orElseThrow(null);
        if (u != null) {
            throw new BadRequestException("User not found");
        }
        Role r = roleJpa.findById(Long.parseLong(idRole.toString())).orElseThrow(
                () -> new BadRequestException("Role not found"));
        if (r.getName().equals(ERole.ROLE_ADMIN)) {
            throw new BadRequestException("Cannot revert role admin");
        }
        try {
            userRepository.revertRole(idUser, idRole);
        } catch (Exception e) {
            throw new SystemException("Error when revert role " + e.getMessage());
        }
        String newRfToken = jwtUtils.generateRefreshToken(u.getUsername(), u.getId(), u.getRoles());
        customCache.put(u.getId().toString(), newRfToken);
    }

    private Map<String, Boolean> convertToMap(String roles) {
        Map<String, Boolean> mapRoles = new HashMap<>();
        Arrays.stream(ERole.values()).forEach(role -> {
            mapRoles.put(role.name(), roles.contains(role.name()));
        });
        return mapRoles;
    }
}
