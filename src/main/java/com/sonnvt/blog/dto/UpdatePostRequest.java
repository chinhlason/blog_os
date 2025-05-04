package com.sonnvt.blog.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdatePostRequest {
    private long id;
    private String title;
    private String content;
    private List<Long> tagIds;
}
