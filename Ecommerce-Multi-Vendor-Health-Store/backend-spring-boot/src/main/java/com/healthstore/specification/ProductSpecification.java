package com.healthstore.specification;

import com.healthstore.dto.SearchFilterDTO;
import com.healthstore.model.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Specification class for building dynamic product search queries.
 */
public class ProductSpecification {

    /**
     * Creates a specification for filtering products based on the provided filters.
     * @param filters The search filters
     * @return A Specification for the Product entity
     */
    public static Specification<Product> filterBy(SearchFilterDTO filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Keyword search in name or description
            if (StringUtils.hasText(filters.getKeyword())) {
                String likeKeyword = "%" + filters.getKeyword().toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), 
                    likeKeyword
                );
                
                Predicate descriptionPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")), 
                    likeKeyword
                );
                
                predicates.add(criteriaBuilder.or(namePredicate, descriptionPredicate));
            }

            // Price range filtering
            if (filters.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("price"), 
                    filters.getMinPrice()
                ));
            }

            if (filters.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("price"), 
                    filters.getMaxPrice()
                ));
            }

            // Category filtering
            if (filters.getCategoryIds() != null && !filters.getCategoryIds().isEmpty()) {
                predicates.add(root.get("category").get("id").in(filters.getCategoryIds()));
            }

            // Combine all predicates with AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
