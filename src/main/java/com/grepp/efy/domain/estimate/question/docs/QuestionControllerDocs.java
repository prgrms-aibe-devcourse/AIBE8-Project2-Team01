package com.grepp.efy.domain.estimate.question.docs;

import com.grepp.efy.domain.estimate.question.dto.QuestionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "요청서 질문", description = "요청서 작성에 필요한 질문 조회 API")
public interface QuestionControllerDocs {

    @Operation(
            summary = "요청서 질문 목록 조회",
            description = "로그인한 사용자가 요청서 공통 질문 목록을 displayOrder 오름차순으로 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "질문 목록 조회 성공")
    @ApiResponse(responseCode = "401", description = "인증 실패")
    ResponseEntity<List<QuestionResponse>> getQuestions();
}
