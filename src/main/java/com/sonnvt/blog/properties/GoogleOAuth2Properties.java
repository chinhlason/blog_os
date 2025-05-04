package com.sonnvt.blog.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "google.oauth2")
public class GoogleOAuth2Properties {
    private String clientId;
    private String clientSecret;
    private String projectId;
    private String authUri;
    private String tokenUri;
    private String authProviderCertUrl;
    private String redirectUris;
    private String userInfoUri;
}