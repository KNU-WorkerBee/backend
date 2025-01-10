package com.example.workbee.speech.dto;

public class SpeechResponseDto {
    private String transcript;

    public SpeechResponseDto(String transcript) {
        this.transcript = transcript;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }
}
