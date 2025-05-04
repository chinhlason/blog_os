package com.sonnvt.blog.database.repository;

import com.sonnvt.blog.database.dao.UserDashboardDao;
import org.springframework.data.domain.Pageable;

public interface AdminRepository {
    UserDashboardDao getUsers(Pageable pageable);
}
