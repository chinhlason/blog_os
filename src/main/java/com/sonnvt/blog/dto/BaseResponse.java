package com.sonnvt.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseResponse<T> {
    private String code;
    private Object message;
    private Object errMsg;
    private T data;
    private Long totalRecords;
}
