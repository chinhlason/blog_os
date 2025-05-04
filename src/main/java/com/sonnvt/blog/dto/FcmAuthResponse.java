package com.sonnvt.blog.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FcmAuthResponse {
    private long id;
    private String username;
    private String fcmToken;
}
