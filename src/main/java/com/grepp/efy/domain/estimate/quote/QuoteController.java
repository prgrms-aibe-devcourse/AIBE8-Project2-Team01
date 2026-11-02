package com.grepp.efy.domain.estimate.quote;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;
}
