package com.sonnvt.blog.database.repository;

import com.sonnvt.blog.database.entity.Comment;
import com.sonnvt.blog.database.projection.CommentProjection;

import java.util.List;

public interface CommentRepository {
    Comment create(Long idPost, Long idAuthor, String content);
    Comment reply(Long idPost, Long idAuthor, Long idParent, Long idMasterParent, String content);
    Comment get(Long id);
    List<CommentProjection> getInPost(Long idPost, int limit, int offset);
    int countInPost(Long idPost);
    void increaseChildCount(Long idComment);
    int findPageById(Long id);
}
