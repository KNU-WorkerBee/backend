package com.example.workbee.speech;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class SpeechToTextService {

    private static final Logger log = LoggerFactory.getLogger(SpeechToTextService.class);

    public String transcribe(MultipartFile audioFile, int sampleRate) throws IOException {
        log.info("Processing file: {}, size: {} bytes", audioFile.getOriginalFilename(), audioFile.getSize());

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
                log.info("Calling Google Cloud Speech-to-Text API...");
                RecognizeResponse response = speechClient.recognize(config, audio);
                log.info("Google Cloud API call completed");

                return processTranscriptionResponse(response);
            }
        } catch (Exception e) {
            log.error("Error while transcribing audio: {}", e.getMessage(), e);
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
            throw new IllegalArgumentException("지원되지 않는 파일 형식입니다. MP3, WAV, FLAC 형식만 지원합니다.");
        }
    }

    private String processTranscriptionResponse(RecognizeResponse response) {
        StringBuilder transcript = new StringBuilder();
        if (response.getResultsCount() == 0) {
            log.warn("No transcription results found");
            return "";
        }

        response.getResultsList().forEach(result -> {
            if (result.getAlternativesCount() > 0) {
                transcript.append(result.getAlternatives(0).getTranscript());
            }
        });

        log.info("Transcription completed, length: {} characters", transcript.length());
        return transcript.toString();
    }

    private RecognitionConfig.AudioEncoding determineAudioEncoding(String filename) {
        if (filename.endsWith(".mp3")) {
            return RecognitionConfig.AudioEncoding.MP3;
        } else if (filename.endsWith(".flac")) {
            return RecognitionConfig.AudioEncoding.FLAC;
        } else if (filename.endsWith(".wav")) {
            return RecognitionConfig.AudioEncoding.LINEAR16;
        } else {
            throw new IllegalArgumentException("Unsupported file format: " + filename);
        }
    }
}
