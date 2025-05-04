package com.sonnvt.blog.exception.errMsg;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorMessage {
    private String ErrorCode;
    private String ErrorMessage;
}
