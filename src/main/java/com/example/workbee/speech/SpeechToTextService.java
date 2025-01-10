package com.example.workbee.speech;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class SpeechToTextService {

    /**
     * Google Cloud Speech-to-Text를 사용하여 음성 파일을 텍스트로 변환
     *
     * @param audioFile 업로드된 음성 파일
     * @param sampleRate 오디오 샘플링 속도 (예: 16000)
     * @return 변환된 텍스트
     * @throws IOException 파일 처리 중 오류 발생 시
     */
    public String transcribe(MultipartFile audioFile, int sampleRate) throws IOException {
        if (audioFile.isEmpty()) {
            throw new IOException("오디오 파일이 비어 있습니다.");
        }

        // 오디오 파일 데이터 읽기
        ByteString audioData = ByteString.copyFrom(audioFile.getBytes());

        // 파일 확장자를 기반으로 인코딩 설정
        String originalFilename = audioFile.getOriginalFilename();
        RecognitionConfig.AudioEncoding encoding = determineAudioEncoding(originalFilename);

        // RecognitionConfig 설정
        RecognitionConfig recognitionConfig = RecognitionConfig.newBuilder()
                .setEncoding(encoding) // 파일 형식
                .setSampleRateHertz(sampleRate) // 샘플링 속도
                .setLanguageCode("ko-KR") // 한국어 설정
                .build();

        // RecognitionAudio 설정
        RecognitionAudio recognitionAudio = RecognitionAudio.newBuilder()
                .setContent(audioData)
                .build();

        // Google Cloud Speech-to-Text API 호출
        try (SpeechClient speechClient = SpeechClient.create()) {
            RecognizeResponse response = speechClient.recognize(recognitionConfig, recognitionAudio);

            // 결과 처리
            StringBuilder transcript = new StringBuilder();
            for (SpeechRecognitionResult result : response.getResultsList()) {
                transcript.append(result.getAlternatives(0).getTranscript());
            }
            return transcript.toString();
        }
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
