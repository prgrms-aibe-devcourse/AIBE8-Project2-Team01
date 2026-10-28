package com.grepp.efy.domain.basedata.firstregion;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FirstRegionRepository extends JpaRepository<FirstRegion, Long> {

    List<FirstRegion> findAllByOrderByIdAsc();

    boolean existsByNameIgnoreCase(String name);

    boolean existsByCode(String code);
}
