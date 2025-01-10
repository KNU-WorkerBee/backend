package com.example.workbee.lecture.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LectureAnalysisResponse {
    private List<String> summary;
    private List<String> keywords;
    private List<Quiz> quizzes;
}