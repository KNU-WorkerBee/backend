package com.example.workbee.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.SpeechSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import lombok.extern.slf4j.Slf4j;
import java.io.FileNotFoundException;
import java.io.IOException;

@Configuration
@Slf4j
public class GoogleCloudConfig {

    @Value("${google.cloud.credentials.file}")
    private Resource credentialsResource;

    @Bean
    public SpeechSettings speechSettings() {
        try {
            log.info("Google Cloud Speech 설정을 로드합니다...");
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsResource.getInputStream());

            log.info("Google Cloud Speech 설정이 성공적으로 로드되었습니다.");
            return SpeechSettings.newBuilder()
                    .setCredentialsProvider(() -> credentials)
                    .build();

        } catch (FileNotFoundException e) {
            log.error("Google Cloud 인증 파일을 찾을 수 없습니다: {}", e.getMessage());
            throw new RuntimeException("Google Cloud 인증 파일을 찾을 수 없습니다. stt.json 파일이 올바른 위치에 있는지 확인하세요.", e);
        } catch (IOException e) {
            log.error("Google Cloud 인증 파일 읽기 실패: {}", e.getMessage());
            throw new RuntimeException("Google Cloud 인증 파일 읽기에 실패했습니다.", e);
        } catch (Exception e) {
            log.error("Google Cloud Speech 설정 초기화 실패: {}", e.getMessage());
            throw new RuntimeException("Google Cloud Speech 설정을 초기화하는데 실패했습니다.", e);
        }
    }
}
