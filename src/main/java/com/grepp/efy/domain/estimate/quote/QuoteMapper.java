package com.grepp.efy.domain.estimate.quote;

import com.grepp.efy.domain.estimate.quote.dto.QuoteCreateRequest;
import com.grepp.efy.domain.estimate.quote.dto.QuoteCreateResponse;
import com.grepp.efy.domain.estimate.servicerequest.ServiceRequest;
import com.grepp.efy.domain.user.expertprofile.ExpertProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuoteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "expertProfile", source = "expertProfile")
    @Mapping(target = "quoteStatus", constant = "SENT")
    @Mapping(target = "serviceRequest", source = "serviceRequest")
    @Mapping(target = "sentAt", ignore = true)
    @Mapping(target = "description", source = "createRequest.description")
    Quote toEntity(QuoteCreateRequest createRequest, ExpertProfile expertProfile, ServiceRequest serviceRequest);

    @Mapping(target = "quoteId", source = "quote.id")
    QuoteCreateResponse toCreateResponse(Quote quote);
}
