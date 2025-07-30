// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/domain/CurrentAccountTransactionRepository.java

package org.apache.fineract.portfolio.currentaccount.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for CurrentAccountTransaction entity
 * This handles all database operations for current account transactions
 */
public interface CurrentAccountTransactionRepository extends JpaRepository<CurrentAccountTransaction, Long> {

    /**
     * Find all transactions for a specific account
     */
    List<CurrentAccountTransaction> findByCurrentAccountIdOrderByTransactionDateDesc(Long accountId);

    /**
     * Find transactions for an account within a date range
     */
    @Query("SELECT cat FROM CurrentAccountTransaction cat WHERE cat.currentAccount.id = :accountId " +
            "AND cat.transactionDate BETWEEN :fromDate AND :toDate " +
            "ORDER BY cat.transactionDate DESC")
    List<CurrentAccountTransaction> findByAccountIdAndDateRange(@Param("accountId") Long accountId,
                                                                @Param("fromDate") LocalDate fromDate,
                                                                @Param("toDate") LocalDate toDate);

    /**
     * Find transactions by account and transaction type
     */
    List<CurrentAccountTransaction> findByCurrentAccountIdAndTransactionType(Long accountId, Integer transactionType);

    /**
     * Find all non-reversed transactions for an account
     */
    @Query("SELECT cat FROM CurrentAccountTransaction cat WHERE cat.currentAccount.id = :accountId " +
            "AND cat.reversed = false ORDER BY cat.transactionDate DESC")
    List<CurrentAccountTransaction> findNonReversedByAccountId(@Param("accountId") Long accountId);

    /**
     * Find transactions for a specific date
     */
    List<CurrentAccountTransaction> findByCurrentAccountIdAndTransactionDate(Long accountId, LocalDate transactionDate);

    /**
     * Find the last transaction for an account
     */
    @Query("SELECT cat FROM CurrentAccountTransaction cat WHERE cat.currentAccount.id = :accountId " +
            "ORDER BY cat.transactionDate DESC")
    List<CurrentAccountTransaction> findLastTransactionByAccountId(@Param("accountId") Long accountId);

    /**
     * Calculate total transaction amount for an account on a specific date
     */
    @Query("SELECT COALESCE(SUM(cat.amount), 0) FROM CurrentAccountTransaction cat " +
            "WHERE cat.currentAccount.id = :accountId AND cat.transactionDate = :transactionDate " +
            "AND cat.reversed = false")
    java.math.BigDecimal sumTransactionAmountByAccountIdAndDate(@Param("accountId") Long accountId,
                                                                @Param("transactionDate") LocalDate transactionDate);
}
