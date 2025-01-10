package com.example.workbee.auth;

import com.example.workbee.auth.dto.LoginRequest;
import com.example.workbee.auth.dto.TokenResponse;
import com.example.workbee.config.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            // 실제 구현에서는 여기에 사용자 인증 로직이 필요합니다
            String token = jwtTokenProvider.createToken(loginRequest.getUsername());
            return ResponseEntity.ok(new TokenResponse("Bearer", token));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new TokenResponse("error", "Invalid credentials"));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String bearerToken) {
        try {
            String token = bearerToken.substring(7);
            boolean isValid = jwtTokenProvider.validateToken(token);
            return ResponseEntity.ok(Map.of("valid", isValid));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("valid", false, "error", "Invalid token"));
        }
    }
} 