package com.sonnvt.blog.api;

import com.sonnvt.blog.database.dao.UserDashboardDao;
import com.sonnvt.blog.database.repository.AdminRepository;
import com.sonnvt.blog.dto.BaseResponse;
import com.sonnvt.blog.dto.CreatePostRequest;
import com.sonnvt.blog.dto.CreatePostResponse;
import com.sonnvt.blog.dto.RevertRoleRequest;
import com.sonnvt.blog.dto.UserDashboardResponse;
import com.sonnvt.blog.exception.errMsg.ErrorMessages;
import com.sonnvt.blog.service.AdminService;
import com.sonnvt.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final AdminRepository adminRepository;
    private final AdminService adminService;
    private final PostService postService;

    @GetMapping
    public UserDashboardDao getUsers(@ParameterObject Pageable pageable) {
        return adminRepository.getUsers(pageable);
    }

    @GetMapping("/admin/users")
    public ResponseEntity<BaseResponse<List<UserDashboardResponse.UserData>>> get(@ParameterObject Pageable pageable) {
        UserDashboardResponse userDashboardResponse = adminService.getUsers(pageable);
        return ResponseEntity.ok(BaseResponse.<List<UserDashboardResponse.UserData>>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data(userDashboardResponse.getUsers())
                .totalRecords(userDashboardResponse.getTotalRecords())
                .build());
    }

    @PostMapping("/admin/post/create")
    public ResponseEntity<BaseResponse<CreatePostResponse>> createPost(@RequestBody CreatePostRequest request) {
        return ResponseEntity.ok(BaseResponse.<CreatePostResponse>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data(postService.createGlobalPost(request)).build());
    }

    @PostMapping("/admin/revert-role")
    public ResponseEntity<BaseResponse<String>> revertRole(@RequestBody RevertRoleRequest request) {
        adminService.revertRole(request.getIdUser(), request.getIdRole());
        return ResponseEntity.ok(BaseResponse.<String>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data("Revert role successfully").build());
    }

    @DeleteMapping("/admin/post/delete")
    public ResponseEntity<BaseResponse<String>> deletePost(@RequestBody Long id) {
        postService.forceDelete(id);
        return ResponseEntity.ok(BaseResponse.<String>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data("Delete post successfully").build());
    }
}
