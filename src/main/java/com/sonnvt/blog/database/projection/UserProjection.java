package com.sonnvt.blog.database.projection;

public interface UserProjection {
    long getId();
    String getFirstName();
    String getLastName();
    String getAvatar();
    String getEmail();
    String getUsername();
    String getPassword();
    Long getFollowing();
    Long getFollowers();
    String getQuotes();
    String getRoles();
    String getCreatedAt();
    String getUpdatedAt();
}
