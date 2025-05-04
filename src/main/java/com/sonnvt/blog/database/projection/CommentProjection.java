package com.sonnvt.blog.database.projection;

public interface CommentProjection {
    long getId();
    long getIdPost();
    String getContent();
    String getCreatedAt();
    String getUpdatedAt();
    Long getChildCount();
    String getAuthor();
    String getReplies();
}
