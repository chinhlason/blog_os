package com.sonnvt.blog.service;

import com.sonnvt.blog.dto.AuthResponse;
import com.sonnvt.blog.dto.FcmAuthResponse;
import com.sonnvt.blog.dto.LoginRequest;
import com.sonnvt.blog.dto.RegisterRequest;
import com.sonnvt.blog.dto.TokenResponse;
import org.antlr.v4.runtime.Token;

public interface AuthService {
    AuthResponse googleLoginWithCode(String code);
    AuthResponse login(LoginRequest request, String clientIp);
    AuthResponse register(RegisterRequest request);
    TokenResponse refreshToken(String rfToken);
}
