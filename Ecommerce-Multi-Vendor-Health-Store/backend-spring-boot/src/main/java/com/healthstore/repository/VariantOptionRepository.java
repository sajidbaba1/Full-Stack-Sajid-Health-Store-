package com.healthstore.repository;

import com.healthstore.model.VariantOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariantOptionRepository extends JpaRepository<VariantOption, Long> {
    
    List<VariantOption> findByProductVariantId(Long productVariantId);
    
    List<VariantOption> findByOptionNameAndOptionValue(String optionName, String optionValue);
}
