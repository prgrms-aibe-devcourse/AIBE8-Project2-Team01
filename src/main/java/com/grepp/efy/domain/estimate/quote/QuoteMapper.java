package com.grepp.efy.domain.estimate.quote;

import com.grepp.efy.domain.estimate.quote.dto.QuoteCreateRequest;
import com.grepp.efy.domain.estimate.quote.dto.QuoteCreateResponse;
import com.grepp.efy.domain.estimate.quote.dto.QuoteRequestResponse;
import com.grepp.efy.domain.estimate.quote.dto.QuoteResponse;
import com.grepp.efy.domain.estimate.servicerequest.ServiceRequest;
import com.grepp.efy.domain.user.expertprofile.ExpertProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuoteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "expertProfile", source = "expertProfile")
    @Mapping(target = "quoteStatus", constant = "SENT")
    @Mapping(target = "serviceRequest", source = "serviceRequest")
    @Mapping(target = "sentAt", ignore = true)
    @Mapping(target = "description", source = "createRequest.description")
    Quote toEntity(QuoteCreateRequest createRequest, ExpertProfile expertProfile, ServiceRequest serviceRequest);

    @Mapping(target = "quoteId", source = "id")
    QuoteCreateResponse toCreateResponse(Quote quote);

    @Mapping(target = "quoteId", source = "id")
    @Mapping(target = "serviceRequestId", source = "serviceRequest.id")
    @Mapping(target = "secondCategoryName", source = "serviceRequest.secondCategory.name")
    @Mapping(target = "secondRegionName", source = "serviceRequest.secondRegion.name")
    @Mapping(target = "expertProfileId", source = "expertProfile.id")
    @Mapping(target = "expertNickname", source = "expertProfile.member.nickname")
    QuoteResponse toResponse(Quote quote);

    List<QuoteResponse> toResponseList(List<Quote> quotes);

    @Mapping(target = "quoteId", source = "id")
    @Mapping(target = "expertProfileId", source = "expertProfile.id")
    @Mapping(target = "expertNickname", source = "expertProfile.member.nickname")
    QuoteRequestResponse toRequestResponse(Quote quote);

    List<QuoteRequestResponse> toRequestResponseList(List<Quote> quotes);
}
