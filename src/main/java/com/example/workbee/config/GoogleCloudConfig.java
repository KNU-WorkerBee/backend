package com.example.workbee.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.SpeechSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class GoogleCloudConfig {

    @Value("classpath:stt.json")
    private Resource credentialsResource;

    @Bean
    public SpeechSettings speechSettings() {
        try {
            return SpeechSettings.newBuilder()
                    .setCredentialsProvider(() -> GoogleCredentials.fromStream(credentialsResource.getInputStream()))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Google Cloud Speech 설정 로드 실패: " + e.getMessage(), e);
        }
    }
}
