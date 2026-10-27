package com.grepp.efy.domain.basedata.firstcategory;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FirstCategoryRepository extends JpaRepository<FirstCategory, Long> {

    List<FirstCategory> findAllByOrderByIdAsc();

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
