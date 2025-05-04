package com.sonnvt.blog.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String firstName;
    private String lastName;
    private String avatar;
    private String email;
    private String username;
    private String token;
    private String refreshToken;
}
