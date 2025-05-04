package com.sonnvt.blog.database.repository;

import com.sonnvt.blog.database.entity.Tag;

import java.util.List;

public interface TagRepository {
    Tag create(String name);
    void delete(long id);
    List<Tag> getByPostsNumberDesc(int limit);
    List<Tag> getInList(List<Long> ids);
    void updateInPost(long postId, List<Long> tagIds);
}
