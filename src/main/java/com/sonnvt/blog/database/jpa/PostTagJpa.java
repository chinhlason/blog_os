package com.sonnvt.blog.database.jpa;

import com.sonnvt.blog.database.entity.PostTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTagJpa extends JpaRepository<PostTags, Long> {
    @Modifying
    @Query(value = "DELETE FROM post_tags WHERE id_tag = :tagId AND id_post = :postId", nativeQuery = true)
    void deleteByTagIdAndPostId(@Param("tagId") Long tagId, @Param("postId") Long postId);
}
