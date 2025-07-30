// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/domain/CurrentAccountTransaction.java

package org.apache.fineract.portfolio.currentaccount.domain;

import jakarta.persistence.*;
import lombok.Setter;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity class representing a Current Account Transaction
 * This records all the financial movements in a current account
 */
@Entity
@Table(name = "m_current_account_transaction")
public class CurrentAccountTransaction extends AbstractPersistableCustom {

    // Relationship to the account
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_account_id", nullable = false)
    private CurrentAccount currentAccount;

    // Transaction details
    @Column(name = "transaction_type_enum", nullable = false)
    private Integer transactionType;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal amount;

    // Setter methods (needed for updating balances)
    // Balance tracking
    @Setter
    @Column(name = "running_balance_derived", scale = 6, precision = 19, nullable = false)
    private BigDecimal runningBalance;

    @Setter
    @Column(name = "cumulative_balance_derived", scale = 6, precision = 19, nullable = false)
    private BigDecimal cumulativeBalance;

    // Transaction metadata
    @Column(name = "is_reversed", nullable = false)
    private boolean reversed;

    @Column(name = "ref_no")
    private String referenceNumber;

    @Column(name = "note")
    private String note;

    // Transaction type constants
    public static final Integer TRANSACTION_TYPE_DEPOSIT = 1;
    public static final Integer TRANSACTION_TYPE_WITHDRAWAL = 2;
    public static final Integer TRANSACTION_TYPE_FEE = 3;
    public static final Integer TRANSACTION_TYPE_INTEREST_CREDIT = 4;
    public static final Integer TRANSACTION_TYPE_OVERDRAFT_INTEREST = 5;

    // Default constructor for JPA
    protected CurrentAccountTransaction() {
        // Empty constructor
    }

    // Private constructor for creating transactions
    private CurrentAccountTransaction(final CurrentAccount currentAccount, final Integer transactionType,
                                      final BigDecimal amount, final LocalDate transactionDate, final String referenceNumber,
                                      final String note) {

        this.currentAccount = currentAccount;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.referenceNumber = referenceNumber;
        this.note = note;
        this.reversed = false;

        // Calculate balances
        calculateBalances();
    }

    /**
     * Static factory method to create a deposit transaction
     */
    public static CurrentAccountTransaction deposit(final CurrentAccount currentAccount, final BigDecimal amount,
                                                    final LocalDate transactionDate, final String referenceNumber, final String note) {

        return new CurrentAccountTransaction(currentAccount, TRANSACTION_TYPE_DEPOSIT, amount,
                transactionDate, referenceNumber, note);
    }

    /**
     * Static factory method to create a withdrawal transaction
     */
    public static CurrentAccountTransaction withdrawal(final CurrentAccount currentAccount, final BigDecimal amount,
                                                       final LocalDate transactionDate, final String referenceNumber, final String note) {

        return new CurrentAccountTransaction(currentAccount, TRANSACTION_TYPE_WITHDRAWAL, amount,
                transactionDate, referenceNumber, note);
    }

    /**
     * Static factory method to create a fee transaction
     */
    public static CurrentAccountTransaction fee(final CurrentAccount currentAccount, final BigDecimal amount,
                                                final LocalDate transactionDate, final String referenceNumber, final String note) {

        return new CurrentAccountTransaction(currentAccount, TRANSACTION_TYPE_FEE, amount,
                transactionDate, referenceNumber, note);
    }

    /**
     * Static factory method to create an interest credit transaction
     */
    public static CurrentAccountTransaction interestCredit(final CurrentAccount currentAccount, final BigDecimal amount,
                                                           final LocalDate transactionDate, final String referenceNumber, final String note) {

        return new CurrentAccountTransaction(currentAccount, TRANSACTION_TYPE_INTEREST_CREDIT, amount,
                transactionDate, referenceNumber, note);
    }

    /**
     * Calculate running and cumulative balances
     * This is called when creating a new transaction
     */
    private void calculateBalances() {
        BigDecimal currentBalance = this.currentAccount.getAccountBalance();

        if (isDebitTransaction()) {
            this.runningBalance = currentBalance.subtract(this.amount);
        } else {
            this.runningBalance = currentBalance.add(this.amount);
        }

        // For simplicity, cumulative balance is same as running balance
        // In a real implementation, you might want to calculate this differently
        this.cumulativeBalance = this.runningBalance;
    }

    /**
     * Check if this is a debit transaction (reduces balance)
     */
    public boolean isDebitTransaction() {
        return this.transactionType.equals(TRANSACTION_TYPE_WITHDRAWAL) ||
                this.transactionType.equals(TRANSACTION_TYPE_FEE) ||
                this.transactionType.equals(TRANSACTION_TYPE_OVERDRAFT_INTEREST);
    }

    /**
     * Check if this is a credit transaction (increases balance)
     */
    public boolean isCreditTransaction() {
        return this.transactionType.equals(TRANSACTION_TYPE_DEPOSIT) ||
                this.transactionType.equals(TRANSACTION_TYPE_INTEREST_CREDIT);
    }

    /**
     * Reverse this transaction
     * This is used for correcting mistakes or canceling transactions
     */
    public void reverse() {
        if (this.reversed) {
            throw new IllegalStateException("Transaction is already reversed");
        }
        this.reversed = true;
    }

    /**
     * Get transaction type as readable string
     */
    public String getTransactionTypeAsString() {
        switch (this.transactionType) {
            case 1:
                return "Deposit";
            case 2:
                return "Withdrawal";
            case 3:
                return "Fee";
            case 4:
                return "Interest Credit";
            case 5:
                return "Overdraft Interest";
            default:
                return "Unknown";
        }
    }

    // Getter methods
    public CurrentAccount getCurrentAccount() {
        return this.currentAccount;
    }

    public Integer getTransactionType() {
        return this.transactionType;
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

}