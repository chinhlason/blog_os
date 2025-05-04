package com.sonnvt.blog.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class FirebaseConfig {
    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException {
        GoogleCredentials credential = GoogleCredentials.fromStream(
                new ClassPathResource("firebase-admin-sdk.json").getInputStream());
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credential).build();
        FirebaseApp app = FirebaseApp.initializeApp(options, "my-app");
        return FirebaseMessaging.getInstance(app);
    }
}
