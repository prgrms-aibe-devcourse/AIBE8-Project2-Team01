package com.grepp.efy.domain.estimate.requestanswer;

import com.grepp.efy.domain.estimate.question.Question;
import com.grepp.efy.domain.estimate.servicerequest.ServiceRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RequestAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_request_id", nullable = false)
    private ServiceRequest serviceRequest;

    @Column(nullable = false, length = 100)
    private String questionContent;

    @Column(nullable = false, length = 500)
    private String content;
}
