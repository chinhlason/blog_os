package com.sonnvt.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class CreatePostResponse {
    private long id;
    private String title;
    private Integer views;
    private String createdAt;
    private String updatedAt;
    private Long idAuthor;
    private List<Tag> tags;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tag {
        private long id;
        private String name;
    }
}
