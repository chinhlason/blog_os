package com.sonnvt.blog.database.repository;

import com.sonnvt.blog.database.entity.Bookmark;

public interface BookmarkRepository {
    void save(long idUser, long idPost);
    Bookmark findByUserIdAndPostId(long idUser, long idPost);
}
