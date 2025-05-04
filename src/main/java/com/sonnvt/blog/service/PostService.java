package com.sonnvt.blog.service;

import com.sonnvt.blog.dto.*;
import com.sonnvt.blog.service.implement.PostServiceImplement;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface PostService {
    TagResponse createTag(String name);
    List<TagResponse> getTag(int limit);
    String uploadImage(MultipartFile file);
    CreatePostResponse createPost(CreatePostRequest createPostRequest);
    CreatePostResponse createGlobalPost(CreatePostRequest createPostRequest);
    PostServiceImplement.GetPostResponseAndTotalRecord getPost(int page, int size);
    GetPostResponse getPostById(Long id);
    CreateCommentResponse createComment(CreateCommentRequest request);
    CreateCommentResponse replyComment(ReplyCommentRequest request);
    PostServiceImplement.GetCommentResponseAndTotalRecord getCommentsInPost(Long idPost, int page, int size);
    Map<String, Object> findCommentPosition(Long idComment);
    PostServiceImplement.GetPostResponseAndTotalRecord searchPost(String keyword, int page, int size);
    void bookmark(Long idPost);
    PostServiceImplement.GetPostResponseAndTotalRecord getPostsByOption(String option, int page, int size);
    PostServiceImplement.GetPostResponseAndTotalRecord getPostsBookmark(int page, int size);
    void increaseView(Long id);
    void update(UpdatePostRequest request);
    void delete(Long id);
    void forceDelete(Long id);
    String report(Long idPost);
}
