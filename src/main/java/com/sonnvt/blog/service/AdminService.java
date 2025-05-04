package com.sonnvt.blog.service;

import com.sonnvt.blog.dto.UserDashboardResponse;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    UserDashboardResponse getUsers(Pageable pageable);
    void revertRole(Long idUser, Integer idRole);
}
