package com.sonnvt.blog.database.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDataDao {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String roles;
    private String createdAt;
}
