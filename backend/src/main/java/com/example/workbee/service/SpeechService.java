package com.example.workbee.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpeechService {
    
    public String convertSpeechToText(MultipartFile audioFile) {
        try (SpeechClient speechClient = SpeechClient.create()) {
            byte[] audioBytes = audioFile.getBytes();
            ByteString audioData = ByteString.copyFrom(audioBytes);

            RecognitionConfig config = RecognitionConfig.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                .setLanguageCode("ko-KR")
                .setSampleRateHertz(16000)
                .setEnableAutomaticPunctuation(true)
                .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder()
                .setContent(audioData)
                .build();

            RecognizeResponse response = speechClient.recognize(config, audio);
            
            StringBuilder transcription = new StringBuilder();
            for (SpeechRecognitionResult result : response.getResultsList()) {
                transcription.append(result.getAlternatives(0).getTranscript());
            }

            return transcription.toString();
        } catch (Exception e) {
            throw new RuntimeException("음성 변환 중 오류 발생: " + e.getMessage());
        }
    }
} 