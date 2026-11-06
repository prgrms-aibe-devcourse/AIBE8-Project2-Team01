package com.grepp.efy.domain.estimate.question;

import com.grepp.efy.domain.estimate.question.docs.QuestionControllerDocs;
import com.grepp.efy.domain.estimate.question.dto.QuestionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class QuestionController implements QuestionControllerDocs {

    private final QuestionService questionService;

    @Override
    @GetMapping("/questions")
    public ResponseEntity<List<QuestionResponse>> getQuestions() {
        List<QuestionResponse> questionResponse = questionService.getQuestions();
        return ResponseEntity.ok(questionResponse);
    }
}
