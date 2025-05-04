package com.sonnvt.blog.database.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDao {
    private long id;
    private String title;
    private String content;
    private int views;
    private int commentCount;
    private String author;
    private String tags;
    private String createdAt;
    private String updatedAt;
}
