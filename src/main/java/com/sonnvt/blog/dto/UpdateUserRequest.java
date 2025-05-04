package com.sonnvt.blog.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateUserRequest {
    String firstName;
    String lastName;
    MultipartFile avatar;
}
