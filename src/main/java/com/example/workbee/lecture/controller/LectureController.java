package com.example.workbee.lecture.controller;

import com.example.workbee.lecture.dto.LectureAnalysisResponse;
import com.example.workbee.lecture.service.LectureAnalysisService;
import com.example.workbee.speech.SpeechToTextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/lectures")
@RequiredArgsConstructor
public class LectureController {

    private final SpeechToTextService speechToTextService;
    private final LectureAnalysisService lectureAnalysisService;

    @PostMapping("/analyze")
    public ResponseEntity<LectureAnalysisResponse> analyzeLecture(
            @RequestParam("audio") MultipartFile audioFile) {
        
        // STT로 텍스트 추출
        String lectureText = speechToTextService.convertSpeechToText(audioFile);
        
        // 텍스트 분석 (요약, 키워드 추출, 퀴즈 생성)
        LectureAnalysisResponse analysis = lectureAnalysisService.analyzeLecture(lectureText);
        
        return ResponseEntity.ok(analysis);
    }
} 