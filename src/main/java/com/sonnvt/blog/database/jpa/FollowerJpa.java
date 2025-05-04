package com.sonnvt.blog.database.jpa;

import com.sonnvt.blog.database.entity.Follower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowerJpa extends JpaRepository<Follower, Long> {
    @Query(value = "SELECT f.id_user FROM followers f WHERE f.id_following = :idUser", nativeQuery = true)
    List<Long> findAllByIdUser(long idUser);

    @Query(value = "SELECT COUNT(*) FROM followers f WHERE f.id_following = :idUser", nativeQuery = true)
    Long countFollowers(long idUser);

    @Query(value = "SELECT COUNT(*) FROM followers f WHERE f.id_user = :idUser", nativeQuery = true)
    Long countFollowing(long idUser);
}
