package com.example.workbee.lecture.service;

import com.example.workbee.lecture.dto.LectureAnalysisResponse;
import com.example.workbee.lecture.dto.Quiz;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureAnalysisService {

    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper;

    public LectureAnalysisResponse analyzeLecture(String lectureText) {
        // 요약 및 키워드 추출
        String summaryPrompt = String.format(
            "다음 강의 내용을 3줄로 요약하고, 핵심 키워드 5개를 추출해주세요. 다음과 같은 JSON 형식으로 응답해주세요:\n" +
            "{\n" +
            "  \"summary\": [\"요약1\", \"요약2\", \"요약3\"],\n" +
            "  \"keywords\": [\"키워드1\", \"키워드2\", \"키워드3\", \"키워드4\", \"키워드5\"]\n" +
            "}\n\n" +
            "강의 내용:\n%s", lectureText
        );

        String summaryResponse = getGptResponse(summaryPrompt);
        SummaryResult summaryResult = parseSummaryResponse(summaryResponse);

        // 퀴즈 생성
        String quizPrompt = String.format(
            "앞서 추출한 요약과 키워드를 바탕으로 3개의 퀴즈를 만들어주세요. " +
            "2개는 객관식, 1개는 주관식으로 만들어주세요. 다음 JSON 형식으로 응답해주세요:\n" +
            "{\n" +
            "  \"quizzes\": [\n" +
            "    {\n" +
            "      \"question\": \"문제\",\n" +
            "      \"correctAnswer\": \"정답\",\n" +
            "      \"options\": [\"보기1\", \"보기2\", \"보기3\", \"보기4\"],\n" +
            "      \"type\": \"MULTIPLE_CHOICE\"\n" +
            "    }\n" +
            "  ]\n" +
            "}\n\n" +
            "요약:\n%s\n\n키워드:\n%s",
            String.join("\n", summaryResult.getSummary()),
            String.join(", ", summaryResult.getKeywords())
        );

        String quizResponse = getGptResponse(quizPrompt);
        List<Quiz> quizzes = parseQuizResponse(quizResponse);

        return new LectureAnalysisResponse(
            summaryResult.getSummary(),
            summaryResult.getKeywords(),
            quizzes
        );
    }

    private String getGptResponse(String prompt) {
        ChatCompletionRequest request = ChatCompletionRequest.builder()
            .model("gpt-3.5-turbo")
            .messages(Arrays.asList(new ChatMessage("user", prompt)))
            .build();

        return openAiService.createChatCompletion(request)
            .getChoices().get(0).getMessage().getContent();
    }

    private SummaryResult parseSummaryResponse(String response) {
        try {
            return objectMapper.readValue(response, SummaryResult.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse GPT response", e);
        }
    }

    private List<Quiz> parseQuizResponse(String response) {
        try {
            QuizResult quizResult = objectMapper.readValue(response, QuizResult.class);
            return quizResult.getQuizzes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse GPT response", e);
        }
    }

    @Getter
    @NoArgsConstructor
    private static class SummaryResult {
        private List<String> summary;
        private List<String> keywords;
    }

    @Getter
    @NoArgsConstructor
    private static class QuizResult {
        private List<Quiz> quizzes;
    }
} 