package com.sonnvt.blog.service;

import com.sonnvt.blog.dto.BaseResponse;
import com.sonnvt.blog.dto.UpdateUserRequest;
import com.sonnvt.blog.dto.UserInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {
    UserInfoResponse getCurrentUserInfo();
    Page<UserInfoResponse> queryUserInfo(String query, Pageable pageable);
    String updateUser(UpdateUserRequest request);
    String follow(Long idFollowing);
    List<UserInfoResponse> getFollowers(Long id, int page, int size);
    List<UserInfoResponse> getFollowing(Long id, int page, int size);
    Long countFollowers(Long id);
    Long countFollowing(Long id);
}
