package com.sonnvt.blog.database.repository.implement;

import com.sonnvt.blog.database.entity.PostTags;
import com.sonnvt.blog.database.entity.Tag;
import com.sonnvt.blog.database.jpa.PostJpa;
import com.sonnvt.blog.database.jpa.PostTagJpa;
import com.sonnvt.blog.database.jpa.TagJpa;
import com.sonnvt.blog.database.repository.PostRepository;
import com.sonnvt.blog.database.repository.TagRepository;
import com.sonnvt.blog.exception.ex.CreateTagException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TagRepositoryImplement implements TagRepository {
    private final TagJpa tagJpa;
    private final PostJpa postJpa;
    private final PostTagJpa postTagJpa;
    private final PostRepository postRepository;

    @Override
    public Tag create(String name) {
        if (tagJpa.existsTagByName(name)) {
            throw new CreateTagException("Tag name is exist");
        }
        return tagJpa.save(Tag.builder().name(name).build());
    }

    @Override
    public void delete(long id) {

    }

    @Override
    public List<Tag> getByPostsNumberDesc(int limit) {
        return tagJpa.findByPostsNumberDesc(limit);
    }

    @Override
    public List<Tag> getInList(List<Long> ids) {
        return tagJpa.findByIdIn(ids);
    }

    @Override
    @Transactional
    public void updateInPost(long postId, List<Long> tagIds) {
        List<PostTags> postTags = postJpa.findAllByIdPost(postId);
        for (PostTags postTag : postTags) {
            if (tagIds.contains(postTag.getTagId())) {
                tagIds.remove(postTag.getTagId());
                continue;
            }
            postTagJpa.deleteByTagIdAndPostId(postTag.getTagId(), postTag.getPostId());
            tagIds.remove(postTag.getTagId());
        }
        postRepository.savePostTag(postId, tagIds);
    }
}
