package com.grepp.efy.domain.basedata.secondregion;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecondRegionRepository extends JpaRepository<SecondRegion, Long> {

    List<SecondRegion> findByFirstRegionIdOrderByIdAsc(Long firstRegionId);

    boolean existsByNameIgnoreCaseAndFirstRegionId(String name, Long firstRegionId);

    boolean existsByCode(String code);
}
