package com.sonnvt.blog.database.repository.implement;

import com.sonnvt.blog.database.entity.Bookmark;
import com.sonnvt.blog.database.jpa.BookmarkJpa;
import com.sonnvt.blog.database.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookmarkRepositoryImplement implements BookmarkRepository {
    private final BookmarkJpa bookmarkJpa;
    @Override
    public void save(long idUser, long idPost) {
        bookmarkJpa.save(Bookmark.builder()
                .idUser(idUser)
                .idPost(idPost)
                .build());
    }

    @Override
    public Bookmark findByUserIdAndPostId(long idUser, long idPost) {
        return bookmarkJpa.findByUserIdAndPostId(idUser, idPost).orElse(null);
    }
}
