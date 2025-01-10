package com.example.workbee.speech;

import com.example.workbee.speech.dto.SpeechResponseDto;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/speech")
public class SpeechController {

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
    public ResponseEntity<SpeechResponseDto> transcribeAudio(@RequestParam("file") @NotNull MultipartFile file) {
        try {
            // STT 실행
            String transcript = speechToTextService.transcribe(file, 16000); // 샘플링 속도 설정

            // 결과 반환
            SpeechResponseDto response = new SpeechResponseDto(transcript);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new SpeechResponseDto("지원되지 않는 파일 형식입니다."));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new SpeechResponseDto("파일 처리 중 오류 발생"));
        }
    }
}
