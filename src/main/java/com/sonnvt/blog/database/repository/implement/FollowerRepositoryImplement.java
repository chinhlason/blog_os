package com.sonnvt.blog.database.repository.implement;

import com.sonnvt.blog.database.entity.Follower;
import com.sonnvt.blog.database.jpa.FollowerJpa;
import com.sonnvt.blog.database.repository.FollowerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FollowerRepositoryImplement implements FollowerRepository {
    private final FollowerJpa followerJpa;
    @Override
    public void save(long idUser, long idFollowing) {
        followerJpa.save(Follower.builder().idUser(idUser).idFollowing(idFollowing).build());
    }

    @Override
    public long countFollowers(long idUser) {
        return followerJpa.countFollowers(idUser);
    }

    @Override
    public long countFollowing(long idUser) {
        return followerJpa.countFollowing(idUser);
    }

    @Override
    public List<Long> findAllFollowerByUser(long idUser) {
        return followerJpa.findAllByIdUser(idUser);
    }
}
