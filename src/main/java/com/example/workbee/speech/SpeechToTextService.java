package com.example.workbee.speech;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
public class SpeechToTextService {

    private final SpeechSettings speechSettings;
    @Value("${google.cloud.project-id}")
    private String projectId;

    public SpeechToTextService(SpeechSettings speechSettings) {
        this.speechSettings = speechSettings;
    }

    /**
     * Google Cloud Speech-to-Text를 사용하여 음성 파일을 텍스트로 변환
     *
     * @param audioFile 업로드된 음성 파일
     * @param sampleRate 오디오 샘플링 속도 (예: 16000)
     * @return 변환된 텍스트
     * @throws IOException 파일 처리 중 오류 발생 시
     */
    public String transcribe(MultipartFile audioFile, int sampleRate) throws IOException {
        log.info("음성 파일 변환 시작: {}, 크기: {} bytes",
                audioFile.getOriginalFilename(), audioFile.getSize());

        validateAudioFile(audioFile);

        try {
            ByteString audioData = ByteString.copyFrom(audioFile.getBytes());
            RecognitionConfig.AudioEncoding encoding = determineAudioEncoding(audioFile.getOriginalFilename());

            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(encoding)
                    .setSampleRateHertz(sampleRate)
                    .setLanguageCode("ko-KR")
                    .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioData)
                    .build();

            try (SpeechClient speechClient = SpeechClient.create()) {
                log.info("Google Cloud Speech API 호출 시작");
                RecognizeResponse response = speechClient.recognize(config, audio);
                log.info("Google Cloud Speech API 호출 완료");

                return processTranscriptionResponse(response);
            }
        } catch (Exception e) {
            log.error("음성 변환 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("음성을 텍스트로 변환하는 중 오류가 발생했습니다.", e);
        }
    }

    private void validateAudioFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("오디오 파일이 비어있습니다.");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".mp3") &&
                !filename.endsWith(".wav") &&
                !filename.endsWith(".flac"))) {
            throw new IllegalArgumentException(
                    "지원되지 않는 파일 형식입니다. MP3, WAV, FLAC 형식만 지원합니다.");
        }
    }

    private String processTranscriptionResponse(RecognizeResponse response) {
        StringBuilder transcript = new StringBuilder();
        if (response.getResultsCount() == 0) {
            log.warn("음성 인식 결과가 없습니다.");
            return "";
        }

        response.getResultsList().forEach(result -> {
            if (result.getAlternativesCount() > 0) {
                transcript.append(result.getAlternatives(0).getTranscript());
            }
        });

        log.info("음성 변환 완료: {} 글자", transcript.length());
        return transcript.toString();
    }

    /**
     * 파일 확장자를 기반으로 인코딩 형식 결정
     *
     * @param filename 파일 이름
     * @return RecognitionConfig.AudioEncoding
     */
    private RecognitionConfig.AudioEncoding determineAudioEncoding(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("파일 이름이 null입니다.");
        }

        if (filename.endsWith(".mp3")) {
            return RecognitionConfig.AudioEncoding.MP3;
        } else if (filename.endsWith(".flac")) {
            return RecognitionConfig.AudioEncoding.FLAC;
        } else if (filename.endsWith(".wav")) {
            return RecognitionConfig.AudioEncoding.LINEAR16;
        } else {
            throw new IllegalArgumentException("지원되지 않는 파일 형식입니다: " + filename);
        }
    }
}
