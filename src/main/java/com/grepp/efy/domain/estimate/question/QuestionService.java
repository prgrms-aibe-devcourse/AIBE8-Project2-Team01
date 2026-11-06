package com.grepp.efy.domain.estimate.question;

import com.grepp.efy.domain.estimate.question.dto.QuestionResponse;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;

    public List<QuestionResponse> getQuestions() {
        return questionMapper.toResponseList(questionRepository.findAllByOrderByDisplayOrderAsc());
    }

    public Question getQuestionEntity(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND_EXCEPTION));
    }

    public List<Question> getQuestionEntities() {
        return questionRepository.findAllByOrderByDisplayOrderAsc();
    }
}
