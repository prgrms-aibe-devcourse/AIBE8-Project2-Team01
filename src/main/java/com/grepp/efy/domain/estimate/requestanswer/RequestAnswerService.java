package com.grepp.efy.domain.estimate.requestanswer;

import com.grepp.efy.domain.estimate.requestanswer.dto.AnswerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestAnswerService {

    private final RequestAnswerRepository requestAnswerRepository;
    private final RequestAnswerMapper requestAnswerMapper;

    public List<AnswerResponse> getRequestAnswerEntity(Long requestId) {
        List<RequestAnswer> answers = requestAnswerRepository.findByServiceRequestId((requestId));

        return requestAnswerMapper.toAnswerResponseList(answers);
    }
}
