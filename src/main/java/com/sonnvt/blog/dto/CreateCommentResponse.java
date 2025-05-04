package com.sonnvt.blog.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCommentResponse {
    private long id;
    private long idPost;
    private long idAuthor;
    private long idMasterParent;
    private long idParent;
    private String content;
    private String createdAt;
    private String updatedAt;
}
