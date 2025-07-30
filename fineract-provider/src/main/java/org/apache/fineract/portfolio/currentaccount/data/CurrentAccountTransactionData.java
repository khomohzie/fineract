// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/data/CurrentAccountTransactionData.java

package org.apache.fineract.portfolio.currentaccount.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Current Account Transaction
 */
public class CurrentAccountTransactionData {

    private final Long id;
    private final Long accountId;
    private final String accountNumber;
    private final Integer transactionType;
    private final String transactionTypeName;
    private final LocalDate transactionDate;
    private final BigDecimal amount;
    private final BigDecimal runningBalance;
    private final BigDecimal cumulativeBalance;
    private final boolean reversed;
    private final String referenceNumber;
    private final String note;
    private final LocalDateTime createdDate;

    public CurrentAccountTransactionData(final Long id, final Long accountId, final String accountNumber,
                                         final Integer transactionType, final String transactionTypeName, final LocalDate transactionDate,
                                         final BigDecimal amount, final BigDecimal runningBalance, final BigDecimal cumulativeBalance,
                                         final boolean reversed, final String referenceNumber, final String note,
                                         final LocalDateTime createdDate) {

        this.id = id;
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.transactionTypeName = transactionTypeName;
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.runningBalance = runningBalance;
        this.cumulativeBalance = cumulativeBalance;
        this.reversed = reversed;
        this.referenceNumber = referenceNumber;
        this.note = note;
        this.createdDate = createdDate;
    }

    // Getter methods
    public Long getId() {
        return this.id;
    }

    public Long getAccountId() {
        return this.accountId;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public Integer getTransactionType() {
        return this.transactionType;
    }

    public String getTransactionTypeName() {
        return this.transactionTypeName;
    }

    public LocalDate getTransactionDate() {
        return this.transactionDate;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public BigDecimal getRunningBalance() {
        return this.runningBalance;
    }

    public BigDecimal getCumulativeBalance() {
        return this.cumulativeBalance;
    }

    public boolean isReversed() {
        return this.reversed;
    }

    public String getReferenceNumber() {
        return this.referenceNumber;
    }

    public String getNote() {
        return this.note;
    }

    public LocalDateTime getCreatedDate() {
        return this.createdDate;
    }
}