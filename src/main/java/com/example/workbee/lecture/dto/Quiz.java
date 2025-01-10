package com.example.workbee.lecture.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {
    private String question;
    private String correctAnswer;
    private List<String> options;  // 객관식 보기
    private String type;  // "MULTIPLE_CHOICE" 또는 "SHORT_ANSWER"
}