package com.grepp.efy.domain.basedata.firstregion;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FirstRegionRepository extends JpaRepository<FirstRegion, Long> {

    List<FirstRegion> findAllByOrderByIdAsc();

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsByCodeAndIdNot(String code, Long id);
}
