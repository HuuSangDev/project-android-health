package com.SelfCare.SelftCare.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("${firebase.service-account-file:firebase/serviceAccountKey.json}")
    private String serviceAccountFile;

    // Hỗ trợ đọc từ environment variable (cho Docker)
    @Value("${firebase.credentials.path:#{null}}")
    private String credentialsPath;

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount = getServiceAccountStream();
                
                if (serviceAccount != null) {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .build();

                    FirebaseApp.initializeApp(options);
                    log.info("Firebase Admin SDK initialized successfully");
                } else {
                    log.warn("Firebase service account file not found. FCM push notifications will be disabled.");
                }
            }
        } catch (IOException e) {
            log.error("Failed to initialize Firebase Admin SDK: {}", e.getMessage());
        }
    }

    private InputStream getServiceAccountStream() {
        try {
            // 1. Ưu tiên đọc từ credentialsPath (Docker mount)
            if (credentialsPath != null && !credentialsPath.isEmpty()) {
                java.io.File file = new java.io.File(credentialsPath);
                if (file.exists()) {
                    log.info("Loading Firebase credentials from external path: {}", credentialsPath);
                    return new FileInputStream(file);
                }
            }

            // 2. Thử đọc từ file system (serviceAccountFile có thể là absolute path)
            java.io.File file = new java.io.File(serviceAccountFile);
            if (file.exists() && file.isAbsolute()) {
                log.info("Loading Firebase credentials from absolute path: {}", serviceAccountFile);
                return new FileInputStream(file);
            }

            // 3. Thử đọc từ classpath
            Resource resource = new ClassPathResource(serviceAccountFile);
            if (resource.exists()) {
                log.info("Loading Firebase credentials from classpath: {}", serviceAccountFile);
                return resource.getInputStream();
            }

            // 4. Thử đọc từ relative file system path
            if (file.exists()) {
                log.info("Loading Firebase credentials from relative path: {}", serviceAccountFile);
                return new FileInputStream(file);
            }

            log.warn("Firebase service account file not found. Checked paths: credentialsPath={}, serviceAccountFile={}", 
                    credentialsPath, serviceAccountFile);
            return null;
        } catch (IOException e) {
            log.error("Error loading Firebase credentials: {}", e.getMessage());
            return null;
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        if (FirebaseApp.getApps().isEmpty()) {
            log.warn("FirebaseApp not initialized. FirebaseMessaging bean will be null.");
            return null;
        }
        return FirebaseMessaging.getInstance();
    }

}
