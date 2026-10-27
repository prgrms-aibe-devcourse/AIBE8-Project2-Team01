package com.grepp.efy.domain.basedata.secondregion;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecondRegionRepository extends JpaRepository<SecondRegion, Long> {

    List<SecondRegion> findByFirstRegionIdOrderByIdAsc(Long firstRegionId);

    boolean existsByNameIgnoreCaseAndFirstRegionIdAndIdNot(String name, Long firstRegionId, Long id);

    boolean existsByCodeAndIdNot(String code, Long id);
}
