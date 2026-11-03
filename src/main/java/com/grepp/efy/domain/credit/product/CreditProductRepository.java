package com.grepp.efy.domain.credit.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CreditProductRepository extends JpaRepository<CreditProduct, Long> {
    List<CreditProduct> findAllByActiveTrueOrderByPriceAsc();

    Optional<CreditProduct> findByIdAndActiveTrue(Long id);
}
