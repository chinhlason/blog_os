package com.sonnvt.blog.database.repository;

import com.sonnvt.blog.database.dao.PostResponseDao;
import com.sonnvt.blog.database.entity.Post;
import com.sonnvt.blog.database.projection.PostProjection;

import java.util.List;

public interface PostRepository {
    Post save(Long idAuthor, String title, String content);
    void savePostTag(Long postId, List<Long> tagIds);
    Post findById(Long id);
    void increaseCommentCount(Long idPost);
    void increaseViewCount(Long idPost);
    void update(Long id, String title, String content);
    List<PostProjection> getPost(int limit, int offset);
    PostProjection getPostById(Long id);
    long countPost();
    List<PostProjection> searchPost(String keyword, int limit, int offset);
    long countPostByKeyword(String keyword);
    List<PostResponseDao> getPostByOption(String option, String value, long idUser, int limit, int offset);
    long countPostByOption(String option, String value, long idUser);
    void delete(Long id);
}
