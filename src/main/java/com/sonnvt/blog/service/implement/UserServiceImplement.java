package com.sonnvt.blog.service.implement;

import com.sonnvt.blog.database.repository.FollowerRepository;
import com.sonnvt.blog.database.repository.UserRepository;
import com.sonnvt.blog.dto.NotificationRequest;
import com.sonnvt.blog.dto.UpdateUserRequest;
import com.sonnvt.blog.dto.UserInfoResponse;
import com.sonnvt.blog.enums.ENotificationType;
import com.sonnvt.blog.exception.ex.BadRequestException;
import com.sonnvt.blog.exception.ex.SystemException;
import com.sonnvt.blog.exception.ex.UploadFileException;
import com.sonnvt.blog.exception.ex.UserNotFoundException;
import com.sonnvt.blog.security.UserPrincipal;
import com.sonnvt.blog.service.FileService;
import com.sonnvt.blog.service.NotificationService;
import com.sonnvt.blog.service.UserService;
import com.sonnvt.blog.utils.Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImplement implements UserService {
    private final UserRepository userRepository;
    private final FileService fileService;
    private final FollowerRepository followerRepository;
    private final NotificationService notificationService;

    @Override
    public UserInfoResponse getCurrentUserInfo() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new SystemException("Error when getting current user info"));
    }

    @Override
    public Page<UserInfoResponse> queryUserInfo(String query, Pageable pageable) {
        if (Utils.isEmpty(query)) {
            return null;
        }
        return userRepository.findByQuery(query, pageable);
    }

    @Override
    @Transactional
    public String updateUser(UpdateUserRequest request) {
        log.info("updateUser {}", request.getFirstName() == null);
        if (Utils.isEmpty(request.getFirstName()) && Utils.isEmpty(request.getLastName())
                && Utils.isEmptyFile(request.getAvatar())) {
            return "No field to update";
        }
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        String avatarUrl = "";
        if (Utils.isEmpty(request.getFirstName())) {
            if (!Objects.requireNonNull(request.getAvatar().getContentType()).startsWith("image/")) {
                throw new UploadFileException("Wrong file type");
            }
            avatarUrl = fileService.upload(request.getAvatar(), true);
        }

        userRepository.update(userPrincipal.getId(), request.getFirstName(), request.getLastName(), avatarUrl);
        return "Update successfully";
    }

    @Override
    public String follow(Long idFollowing) {
        if (idFollowing == null) {
            throw new BadRequestException("Id following is null");
        }
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        UserInfoResponse userInfoResponse = userRepository.findById(idFollowing).orElse(null);
        if (userInfoResponse == null) {
            throw new UserNotFoundException("Error when getting current user info");
        }
        if (userPrincipal.getId().equals(idFollowing)) {
            throw new BadRequestException("Can not follow yourself");
        }
        followerRepository.save(userPrincipal.getId(), idFollowing);
        notificationService.send(NotificationRequest.builder()
                .idRecipient(userInfoResponse.getId())
                .metadata(userPrincipal.getId())
                .notificationType(ENotificationType.FOLLOW).build());
        return "Follow user " + userInfoResponse.getUsername() + " successfully";
    }

    @Override
    public List<UserInfoResponse> getFollowers(Long id, int page, int size) {
        int offset = (page - 1) * size;
        return userRepository.findAllFollowers(id, size, offset);
    }

    @Override
    public List<UserInfoResponse> getFollowing(Long id, int page, int size) {
        int offset = (page - 1) * size;
        return userRepository.findAllFollowing(id, size, offset);
    }

    @Override
    public Long countFollowers(Long id) {
        return followerRepository.countFollowers(id);
    }

    @Override
    public Long countFollowing(Long id) {
        return followerRepository.countFollowing(id);
    }
}
