package com.sonnvt.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCommentRequest {
    @NotNull(message = "Post id is required")
    @Positive(message = "Post id must be positive")
    private long idPost;
    @NotBlank(message = "Content is required")
    private String content;
}
