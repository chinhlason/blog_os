package com.sonnvt.blog.api;

import com.sonnvt.blog.dto.BaseResponse;
import com.sonnvt.blog.dto.UpdateUserRequest;
import com.sonnvt.blog.dto.UserInfoResponse;
import com.sonnvt.blog.exception.errMsg.ErrorMessages;
import com.sonnvt.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping
    public ResponseEntity<BaseResponse<UserInfoResponse>> getUserInfo() {
        return ResponseEntity.ok(BaseResponse.<UserInfoResponse>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data(userService.getCurrentUserInfo())
                .build());
    }

    @PutMapping
    public ResponseEntity<BaseResponse<String>> updateUser(@ModelAttribute  UpdateUserRequest request) {
        return ResponseEntity.ok(BaseResponse.<String>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(userService.updateUser(request))
                .data(null)
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<Page<UserInfoResponse>>> getUserInfoList(@RequestParam String query, @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.<Page<UserInfoResponse>>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data(userService.queryUserInfo(query, pageable))
                .build());
    }

    @PostMapping("/follow/{id}")
    public ResponseEntity<BaseResponse<String>> followUser(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.<String>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(userService.follow(id))
                .data(null)
                .build());
    }
}
