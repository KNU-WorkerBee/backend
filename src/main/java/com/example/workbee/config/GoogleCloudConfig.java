package com.example.workbee.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.SpeechSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class GoogleCloudConfig {

    private static final Logger log = LoggerFactory.getLogger(GoogleCloudConfig.class);

    @Value("${google.cloud.credentials.file}")
    private Resource credentialsResource;

    @Bean
    public SpeechSettings speechSettings() {
        try {
            log.info("Initializing Google Cloud Speech API settings...");
            validateCredentialsFile();

            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsResource.getInputStream());
            log.info("Google Cloud credentials loaded successfully: {}", credentials.getClass().getSimpleName());

            return SpeechSettings.newBuilder()
                    .setCredentialsProvider(() -> credentials)
                    .build();
        } catch (IOException e) {
            log.error("Error loading Google Cloud credentials: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load Google Cloud credentials", e);
        }
    }

    /**
     * Validates that the credentials file is accessible and not null.
     */
    private void validateCredentialsFile() {
        if (credentialsResource == null) {
            log.error("Google Cloud credentials resource is not specified.");
            throw new RuntimeException("Google Cloud credentials resource is not specified.");
        }

        if (!credentialsResource.exists()) {
            log.error("Google Cloud credentials file not found: {}", credentialsResource.getFilename());
            throw new RuntimeException("Google Cloud credentials file not found: " + credentialsResource.getFilename());
        }

        log.info("Google Cloud credentials file validated: {}", credentialsResource.getFilename());
    }
}
