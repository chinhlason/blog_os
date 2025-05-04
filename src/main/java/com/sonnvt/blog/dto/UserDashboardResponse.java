package com.sonnvt.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDashboardResponse {
    List<UserData> users;
    long totalRecords;

    @Data
    @NoArgsConstructor
    public static class UserData {
        private Integer id;
        private String email;
        private String firstName;
        private String lastName;
        private Map<String, Boolean> roles;
        private String createdAt;
    }
}
