package com.grepp.efy.domain.estimate.quote;

import com.grepp.efy.domain.estimate.servicerequest.ServiceRequest;
import com.grepp.efy.domain.user.expertprofile.ExpertProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    boolean existsByExpertProfileAndServiceRequest(ExpertProfile expertProfile, ServiceRequest serviceRequest);
}
