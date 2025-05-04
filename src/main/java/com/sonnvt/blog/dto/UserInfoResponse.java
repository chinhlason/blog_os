package com.sonnvt.blog.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UserInfoResponse {
    private Long id;
    private String username;
    private String email;
    private String avatar;
    private String firstName;
    private String lastName;
    private Long followers;
    private Long following;
    private String quotes;
    @JsonIgnore
    private String password;
    @JsonDeserialize(as = List.class)
    @JsonIgnore
    private String roles;
    private String createdAt;
    private String updatedAt;
}
