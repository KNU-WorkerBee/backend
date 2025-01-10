package com.example.workbee.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import com.example.workbee.service.SpeechService;
import com.example.workbee.dto.ConversionResponse;
import com.example.workbee.dto.ErrorResponse;

@RestController
@RequestMapping("/api/speech")
@RequiredArgsConstructor
public class SpeechController {
    private final SpeechService speechService;

    @PostMapping("/convert")
    public ResponseEntity<?> convertSpeechToText(@RequestParam("file") MultipartFile file) {
        try {
            String text = speechService.convertSpeechToText(file);
            return ResponseEntity.ok(new ConversionResponse(text));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("변환 실패: " + e.getMessage()));
        }
    }
} 