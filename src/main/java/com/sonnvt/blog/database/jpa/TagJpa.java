package com.sonnvt.blog.database.jpa;

import com.sonnvt.blog.database.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagJpa extends JpaRepository<Tag, Long> {
    boolean existsTagByName(String name);

    @Query(value = "SELECT * FROM tags ORDER BY posts_number DESC, name ASC LIMIT :limit", nativeQuery = true)
    List<Tag> findByPostsNumberDesc(@Param("limit") int limit);

    @Query(value = "SELECT * FROM tags WHERE id IN :ids", nativeQuery = true)
    List<Tag> findByIdIn(List<Long> ids);
}
