package com.sonnvt.blog.service.implement;

import com.sonnvt.blog.database.entity.User;
import com.sonnvt.blog.database.repository.UserRepository;
import com.sonnvt.blog.database.repository.UserRolesRepository;
import com.sonnvt.blog.dto.AuthResponse;
import com.sonnvt.blog.dto.LoginRequest;
import com.sonnvt.blog.dto.RegisterRequest;
import com.sonnvt.blog.dto.TokenResponse;
import com.sonnvt.blog.dto.UserInfoResponse;
import com.sonnvt.blog.exception.ex.GoogleOauth2Exception;
import com.sonnvt.blog.exception.ex.LoginException;
import com.sonnvt.blog.exception.ex.RegisterException;
import com.sonnvt.blog.properties.GoogleOAuth2Properties;
import com.sonnvt.blog.security.JwtUtils;
import com.sonnvt.blog.service.AuthService;
import com.sonnvt.blog.cache.PasswordCache;
import com.sonnvt.blog.cache.TokenCache;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImplement implements AuthService {
    private final GoogleOAuth2Properties googleOAuth2Properties;

    private final UserRepository userRepository;
    private final UserRolesRepository userRolesRepository;
    private final PasswordCache passwordCache;
    private final TokenCache tokenCache;

    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Value("${app.default-password}")
    private String defaultPassword;
    private String DEFAULT_ROLE = "ROLE_USER, ROLE_COMMENTATOR, ROLE_WRITER";

    @Value("${app.default-avatar}")
    private String DEFAULT_AVATAR;

    @Override
    @Transactional
    public AuthResponse googleLoginWithCode(String code) {
        String token = exchangeCodeToToken(code);

        String userInfoUri = googleOAuth2Properties.getUserInfoUri();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setBearerAuth(token);
        HttpEntity<Void> authEntity = new HttpEntity<>(authHeaders);
        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(userInfoUri, HttpMethod.GET, authEntity, Map.class);

        String email = Objects.requireNonNull(userInfoResponse.getBody()).get("email").toString();

        UserInfoResponse user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            String jwt = jwtUtils.generateToken(user.getUsername(), user.getId(), user.getRoles().toString());
            return AuthResponse.builder()
                    .username(user.getUsername())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(email)
                    .avatar(user.getAvatar())
                    .token(jwt)
                    .build();
        }

        String emailResp = userInfoResponse.getBody().get("email").toString();
        String username = emailResp.split("@")[0];

        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setEmail(userInfoResponse.getBody().get("email").toString());
        request.setFirstName(userInfoResponse.getBody().get("given_name").toString());
        request.setLastName(userInfoResponse.getBody().get("family_name").toString());
        request.setPassword(defaultPassword);
        request.setAvatar(userInfoResponse.getBody().get("picture").toString());

        return save(request, true);
    }

    @Override
    public AuthResponse login(LoginRequest request, String clientIp) {
        UserInfoResponse user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new LoginException("Login information is incorrect"));
        String key = clientIp + "_" + user.getId();
        log.info("key {}", key);
        boolean isMatching = encoder.matches(request.getPassword(), user.getPassword());
        if (!isMatching) {
            Integer limit = (Integer) passwordCache.get(key);
            if (limit == null) {
                limit = 0;
            }
            if (limit >= 5) {
                passwordCache.remove(key);
                throw new LoginException("Wrong password limit exceeded");
            }
            limit = limit + 1;
            passwordCache.put(key, limit);
            throw new LoginException("Login information is incorrect");
        }
        String jwt = jwtUtils.generateToken(user.getUsername(), user.getId(), user.getRoles());
        String rfToken = jwtUtils.generateRefreshToken(user.getUsername(), user.getId(), user.getRoles());
        tokenCache.put(user.getId().toString(), rfToken);
        return AuthResponse.builder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .token(jwt)
                .refreshToken(rfToken)
                .build();
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        UserInfoResponse user = userRepository.findByUsernameOrEmail(request.getUsername(), request.getEmail()).orElse(null);
        if (user != null) {
            throw new RegisterException("Email or Username is already in use");
        }
        return save(request, false);
    }

    @Override
    public TokenResponse refreshToken(String rfToken) {
        if (rfToken == null || rfToken.isEmpty() || !jwtUtils.isValidRfToken(rfToken)) {
            throw new LoginException("Refresh token is invalid");
        }
        String roles = jwtUtils.extractRfRoles(rfToken);
        Long userId = jwtUtils.extractRfUserId(rfToken);
        String username = jwtUtils.extractRfUsername(rfToken);
        String token = jwtUtils.generateToken(username, userId, roles);

        return new TokenResponse(token);
    }

    private AuthResponse save(RegisterRequest request, Boolean haveToken) {
        User user = userRepository.save(User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .username(request.getUsername())
                .avatar((request.getAvatar() == null || request.getAvatar().isEmpty()) ? DEFAULT_AVATAR : request.getAvatar())
                .password(encoder.encode(request.getPassword()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        userRolesRepository.register(user.getId());
        if (!haveToken) {
            return AuthResponse.builder()
                    .username(user.getUsername())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .avatar(user.getAvatar())
                    .build();
        }
        String jwt = jwtUtils.generateToken(user.getUsername(), user.getId(), DEFAULT_ROLE);
        String rfToken = jwtUtils.generateRefreshToken(user.getUsername(), user.getId(), DEFAULT_ROLE);
        tokenCache.put(user.getId().toString(), rfToken);
        return AuthResponse.builder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .token(jwt)
                .refreshToken(rfToken)
                .build();
    }

    private String exchangeCodeToToken(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        String tokenUri = googleOAuth2Properties.getTokenUri();
        String clientId = googleOAuth2Properties.getClientId();
        String clientSecret = googleOAuth2Properties.getClientSecret();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String body = "code=" + code +
                "&client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&redirect_uri=" + googleOAuth2Properties.getRedirectUris() +
                "&grant_type=authorization_code";

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(tokenUri, HttpMethod.POST, request, Map.class);
            return Objects.requireNonNull(response.getBody()).get("access_token").toString();
        } catch (Exception e) {
            throw new GoogleOauth2Exception("Cannot get access token from google" + e.getMessage());
        }
    }
}
