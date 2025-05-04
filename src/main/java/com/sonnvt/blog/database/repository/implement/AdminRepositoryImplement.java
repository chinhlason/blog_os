package com.sonnvt.blog.database.repository.implement;

import com.sonnvt.blog.database.dao.UserDashboardDao;
import com.sonnvt.blog.database.dao.UserDataDao;
import com.sonnvt.blog.database.repository.AdminRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AdminRepositoryImplement implements AdminRepository {
    @PersistenceContext
    protected EntityManager entityManager;

    @Override
    public UserDashboardDao getUsers(Pageable pageable) {
        String query = "SELECT u.id, u.email, " +
                "u.first_name AS firstName, u.last_name AS lastName, " +
                "STRING_AGG(r.name, ', ') AS roles, " +
                "TO_CHAR(u.created_at, 'YYYY-MM-DD') AS createdAt " +
                "FROM users u " +
                "JOIN public.user_roles ur ON u.id = ur.user_id " +
                "JOIN public.roles r ON ur.role_id = r.id " +
                "GROUP BY u.id";

        String countQuery = "SELECT COUNT (*) FROM users u";

        Query nativeQuery = entityManager.createNativeQuery(query, Tuple.class);
        nativeQuery.setFirstResult((int) pageable.getOffset());
        nativeQuery.setMaxResults(pageable.getPageSize());
        List<Tuple> results = nativeQuery.getResultList();
        List<UserDataDao> userDataList = new ArrayList<>();
        for (Tuple tuple : results) {
            UserDataDao userData = new UserDataDao();
            userData.setId(tuple.get("id", Integer.class));
            userData.setEmail(tuple.get("email", String.class));
            userData.setFirstName(tuple.get("firstName", String.class));
            userData.setLastName(tuple.get("lastName", String.class));
            userData.setRoles(tuple.get("roles", String.class));
            userData.setCreatedAt(tuple.get("createdAt", String.class));
            userDataList.add(userData);
        }

        Query countNativeQuery = entityManager.createNativeQuery(countQuery);
        long totalRecords = ((Number) countNativeQuery.getSingleResult()).longValue();

        return UserDashboardDao.builder()
                .totalRecords(totalRecords)
                .data(userDataList)
                .build();
    }
}
