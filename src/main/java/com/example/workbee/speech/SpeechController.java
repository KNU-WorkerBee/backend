package com.example.workbee.speech;

import com.example.workbee.speech.dto.SpeechResponseDto;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/speech")
public class SpeechController {

    private final Logger log = LoggerFactory.getLogger(SpeechController.class);
    private final SpeechToTextService speechToTextService;

    public SpeechController(SpeechToTextService speechToTextService) {
        this.speechToTextService = speechToTextService;
    }

    /**
     * 음성 파일을 업로드하고 텍스트로 변환
     *
     * @param file 사용자 업로드 파일
     * @return 변환된 텍스트
     */
    @PostMapping("/transcribe")
    public ResponseEntity<SpeechResponseDto> transcribeAudio(
            @RequestParam("file") @NotNull MultipartFile file,
            @RequestHeader Map<String, String> headers) {

        log.info("Request received at /api/speech/transcribe");
        log.info("Request Headers: {}", headers);

        try {
            // 파일 업로드 확인을 위한 로그 및 검증
            log.info("File details: name={}, size={}", file.getOriginalFilename(), file.getSize());
            if (file.isEmpty()) {
                throw new IllegalArgumentException("업로드된 파일이 비어 있습니다.");
            }

            // STT 실행
            String transcript = speechToTextService.transcribe(file, 16000);

            return ResponseEntity.ok(new SpeechResponseDto(transcript));

        } catch (IllegalArgumentException e) {
            log.error("File validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new SpeechResponseDto("파일 검증 실패: " + e.getMessage()));
        } catch (IOException e) {
            log.error("Error processing file: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new SpeechResponseDto("파일 처리 중 오류 발생"));
        } catch (Exception e) {
            log.error("Unhandled exception occurred: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new SpeechResponseDto("알 수 없는 오류 발생"));
        }
    }
}
