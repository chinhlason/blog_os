package com.sonnvt.blog.api;

import com.sonnvt.blog.dto.BaseResponse;
import com.sonnvt.blog.dto.UserInfoResponse;
import com.sonnvt.blog.exception.errMsg.ErrorMessages;
import com.sonnvt.blog.service.UserService;
import com.sonnvt.blog.utils.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicController {
    private final int SIZE = Constant.DEFAULT_SIZE;
    private final UserService userService;

    @GetMapping("/follower/{id}")
    public ResponseEntity<BaseResponse<List<UserInfoResponse>>> getFollowers(@PathVariable Long id,
                                                                             @RequestParam(required = false) Integer page,
                                                                             @RequestParam(required = false) Integer size) {
        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = SIZE;
        }
        return ResponseEntity.ok(BaseResponse.<List<UserInfoResponse>>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data(userService.getFollowers(id, page, size))
                .totalRecords(userService.countFollowers(id))
                .build());
    }

    @GetMapping("/following/{id}")
    public ResponseEntity<BaseResponse<List<UserInfoResponse>>> getFollowing(@PathVariable Long id,
                                                                             @RequestParam(required = false) Integer page,
                                                                             @RequestParam(required = false) Integer size) {
        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = SIZE;
        }
        return ResponseEntity.ok(BaseResponse.<List<UserInfoResponse>>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data(userService.getFollowing(id, page, size))
                .totalRecords(userService.countFollowing(id))
                .build());
    }
}
