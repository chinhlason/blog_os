package com.sonnvt.blog.database.jpa;

import com.sonnvt.blog.database.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkJpa extends JpaRepository<Bookmark, Long> {
    @Query(value = "SELECT * FROM bookmarks WHERE id_user = :idUser AND id_post = :idPost", nativeQuery = true)
    Optional<Bookmark> findByUserIdAndPostId(long idUser, long idPost);
}
