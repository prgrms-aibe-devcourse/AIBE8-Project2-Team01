package com.grepp.efy.domain.estimate.servicerequest;

import com.grepp.efy.domain.basedata.secondcategory.SecondCategory;
import com.grepp.efy.domain.basedata.secondregion.SecondRegion;
import com.grepp.efy.domain.estimate.requestanswer.dto.AnswerResponse;
import com.grepp.efy.domain.estimate.servicerequest.dto.ServiceRequestCreateResponse;
import com.grepp.efy.domain.estimate.servicerequest.dto.ServiceRequestListResponse;
import com.grepp.efy.domain.estimate.servicerequest.dto.ServiceRequestResponse;
import com.grepp.efy.domain.user.member.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServiceRequestMapper {

    ServiceRequestCreateResponse toServiceRequestCreateResponse(ServiceRequest serviceRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "requestStatus", constant = "OPEN")
    ServiceRequest createEntity(Member member, SecondCategory secondCategory, SecondRegion secondRegion);

    @Mapping(target = "requestId", source = "serviceRequest.id")
    @Mapping(target = "customerNickname", source = "member.nickname")
    @Mapping(target = "secondCategoryName", source = "serviceRequest.secondCategory.name")
    @Mapping(target = "secondRegionName", source = "serviceRequest.secondRegion.name")
    @Mapping(target = "createdAt", source = "serviceRequest.createdAt")
    @Mapping(target = "answers", source = "answerResponses")
    ServiceRequestResponse toRequestResponse(Member member, ServiceRequest serviceRequest, List<AnswerResponse> answerResponses);

    @Mapping(target = "requestId", source = "id")
    @Mapping(target = "secondCategoryName", source = "secondCategory.name")
    @Mapping(target = "secondRegionName", source = "secondRegion.name")
    ServiceRequestListResponse toRequestListResponse(ServiceRequest serviceRequest);
    
    List<ServiceRequestListResponse> toRequestListResponse(List<ServiceRequest> serviceRequest);
}
