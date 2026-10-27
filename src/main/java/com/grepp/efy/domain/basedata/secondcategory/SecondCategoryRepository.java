package com.grepp.efy.domain.basedata.secondcategory;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecondCategoryRepository extends JpaRepository<SecondCategory, Long> {

    List<SecondCategory> findByFirstCategoryIdOrderByIdAsc(Long firstCategoryId);

    boolean existsByNameIgnoreCaseAndFirstCategoryIdAndIdNot(String name, Long firstCategoryId, Long id);
}
