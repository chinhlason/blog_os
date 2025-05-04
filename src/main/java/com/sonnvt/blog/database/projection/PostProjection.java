package com.sonnvt.blog.database.projection;

public interface PostProjection {
    long getId();
    String getTitle();
    String getContent();
    int getViews();
    int getCommentCount();
    String getAuthor();
    String getTags();
    String getCreatedAt();
    String getUpdatedAt();
}
