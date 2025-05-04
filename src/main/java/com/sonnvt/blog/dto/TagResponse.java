package com.sonnvt.blog.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagResponse {
    private long id;
    private String name;
    private Integer postsNumber;
    private String createdAt;
    private String updatedAt;
}
