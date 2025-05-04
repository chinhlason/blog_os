package com.sonnvt.blog.database.repository;

import java.util.List;

public interface FollowerRepository {
    void save(long idUser, long idFollowing);
    long countFollowers(long idUser);
    long countFollowing(long idUser);
    List<Long> findAllFollowerByUser(long idUser);
}
