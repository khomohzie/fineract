// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/domain/CurrentAccountRepository.java

package org.apache.fineract.portfolio.currentaccount.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for CurrentAccount entity
 * This handles all database operations for current account instances
 */
public interface CurrentAccountRepository extends JpaRepository<CurrentAccount, Long>,
        JpaSpecificationExecutor<CurrentAccount> {

    /**
     * Find account by account number
     */
    CurrentAccount findByAccountNumber(String accountNumber);

    /**
     * Find account by external ID
     */
    CurrentAccount findByExternalId(String externalId);

    /**
     * Find all accounts for a specific client
     */
    List<CurrentAccount> findByClientId(Long clientId);

    /**
     * Find all accounts for a specific group
     */
    List<CurrentAccount> findByGroupId(Long groupId);

    /**
     * Find all accounts using a specific product
     */
    List<CurrentAccount> findByProductId(Long productId);

    /**
     * Find all active accounts
     */
    @Query("SELECT ca FROM CurrentAccount ca WHERE ca.status = 300")
    List<CurrentAccount> findAllActive();

    /**
     * Find all accounts for a client with a specific status
     */
    List<CurrentAccount> findByClientIdAndStatus(Long clientId, Integer status);

    /**
     * Check if account number exists (excluding a specific ID)
     */
    boolean existsByAccountNumberAndIdNot(String accountNumber, Long id);

    /**
     * Check if external ID exists (excluding a specific ID)
     */
    boolean existsByExternalIdAndIdNot(String externalId, Long id);

    /**
     * Count active accounts for a client
     */
    @Query("SELECT COUNT(ca) FROM CurrentAccount ca WHERE ca.client.id = :clientId AND ca.status = 300")
    Long countActiveAccountsByClientId(@Param("clientId") Long clientId);

    /**
     * Find accounts with low balance (below minimum)
     * This can be used for generating reports or alerts
     */
    @Query("SELECT ca FROM CurrentAccount ca WHERE ca.accountBalance < ca.product.minimumOpeningBalance AND ca.status = 300")
    List<CurrentAccount> findAccountsWithLowBalance();
}