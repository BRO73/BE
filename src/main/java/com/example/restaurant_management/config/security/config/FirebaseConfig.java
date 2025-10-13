package com.example.restaurant_management.config.security.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.admin.credentials.path}")
    private String serviceAccountPath;


    @PostConstruct
    public void init() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            try (FileInputStream serviceAccount = new FileInputStream(serviceAccountPath)) {
                // SDK sẽ tự động đọc projectId từ file JSON
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        // XÓA DÒNG NÀY ĐI
                        // .setProjectId(projectId)
                        .build();
                FirebaseApp.initializeApp(options);
            }
        }
    }
}
