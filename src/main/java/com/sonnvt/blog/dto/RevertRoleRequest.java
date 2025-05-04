package com.sonnvt.blog.dto;

import lombok.Data;

@Data
public class RevertRoleRequest {
    private Long idUser;
    private Integer idRole;
}
