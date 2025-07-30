// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/domain/CurrentAccountProductRepository.java

package org.apache.fineract.portfolio.currentaccount.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repository interface for CurrentAccountProduct entity
 * This handles all database operations for current account products
 * JpaRepository provides basic CRUD operations
 * JpaSpecificationExecutor allows for complex queries
 */
public interface CurrentAccountProductRepository extends JpaRepository<CurrentAccountProduct, Long>,
        JpaSpecificationExecutor<CurrentAccountProduct> {

    /**
     * Find product by name
     * Spring Data JPA will automatically implement this method based on the method name
     */
    CurrentAccountProduct findByName(String name);

    /**
     * Find product by short name
     */
    CurrentAccountProduct findByShortName(String shortName);

    /**
     * Check if a product with given name exists (excluding a specific ID)
     * This is useful when updating products to avoid duplicate names
     */
    boolean existsByNameAndIdNot(String name, Long id);

    /**
     * Check if a product with given short name exists (excluding a specific ID)
     */
    boolean existsByShortNameAndIdNot(String shortName, Long id);
}
