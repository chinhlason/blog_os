package com.sonnvt.blog.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreatePostRequest {
    private String title;
    private String content;
    private List<Long> tagIds;
}
