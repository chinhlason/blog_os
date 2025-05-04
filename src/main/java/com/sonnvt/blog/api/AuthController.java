package com.sonnvt.blog.api;

import com.sonnvt.blog.dto.AuthResponse;
import com.sonnvt.blog.dto.BaseResponse;
import com.sonnvt.blog.dto.LoginRequest;
import com.sonnvt.blog.dto.RegisterRequest;
import com.sonnvt.blog.dto.TokenResponse;
import com.sonnvt.blog.exception.errMsg.ErrorMessages;
import com.sonnvt.blog.service.AuthService;
import com.sonnvt.blog.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/public/callback")
    public ResponseEntity<BaseResponse<AuthResponse>> googleLogin(@RequestParam String code) {
        return ResponseEntity.ok(BaseResponse.<AuthResponse>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data(authService.googleLoginWithCode(code))
                .build());
    }

    @PostMapping("/public/login")
    public ResponseEntity<BaseResponse<AuthResponse>> login(@RequestBody LoginRequest request, HttpServletRequest req) {
        String clientIp = Utils.getClientIp(req);
        return ResponseEntity.ok(BaseResponse.<AuthResponse>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data(authService.login(request, clientIp))
                .build());
    }

    @PostMapping("/public/register")
    public ResponseEntity<BaseResponse<AuthResponse>> register(@RequestBody @Valid RegisterRequest user) {
        return ResponseEntity.ok(BaseResponse.<AuthResponse>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data(authService.register(user))
                .build());
    }

    @PostMapping("/public/refresh-token")
    public ResponseEntity<BaseResponse<TokenResponse>> refreshToken(@CookieValue("refresh-token") String rfToken) {
        return ResponseEntity.ok(BaseResponse.<TokenResponse>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data(authService.refreshToken(rfToken))
                .build());
    }
}
