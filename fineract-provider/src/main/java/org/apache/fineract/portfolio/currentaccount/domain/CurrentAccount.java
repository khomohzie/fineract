// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/domain/CurrentAccount.java

package org.apache.fineract.portfolio.currentaccount.domain;

import jakarta.persistence.*;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.currentaccount.exception.CurrentAccountTransactionNotValidException;
import org.apache.fineract.portfolio.group.domain.Group;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity class representing a Current Account instance
 * This is the actual account that belongs to a client/group
 */
@Entity
@Table(name = "m_current_account")
public class CurrentAccount extends AbstractPersistableCustom {

    // Account identifiers
    @Column(name = "account_no", nullable = false, unique = true)
    private String accountNumber;

    @Column(name = "external_id", unique = true)
    private String externalId;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private CurrentAccountProduct product;

    // Account status and lifecycle
    @Column(name = "status_enum", nullable = false)
    private Integer status;

    @Column(name = "account_type_enum", nullable = false)
    private Integer accountType;

    @Column(name = "submitted_on_date", nullable = false)
    private LocalDate submittedOnDate;

    @Column(name = "approved_on_date")
    private LocalDate approvedOnDate;

    @Column(name = "activated_on_date")
    private LocalDate activatedOnDate;

    @Column(name = "closed_on_date")
    private LocalDate closedOnDate;

    // Currency information
    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @Column(name = "currency_digits", nullable = false)
    private Integer currencyDigits;

    @Column(name = "currency_multiplesof")
    private Integer inMultiplesOf;

    // Financial information - these are derived/calculated fields
    @Column(name = "account_balance_derived", scale = 6, precision = 19, nullable = false)
    private BigDecimal accountBalance;

    @Column(name = "total_deposits_derived", scale = 6, precision = 19, nullable = false)
    private BigDecimal totalDeposits;

    @Column(name = "total_withdrawals_derived", scale = 6, precision = 19, nullable = false)
    private BigDecimal totalWithdrawals;

    @Column(name = "overdraft_limit", scale = 6, precision = 19)
    private BigDecimal overdraftLimit;

    // Transactions relationship
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "currentAccount", orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("transactionDate, id")
    private List<CurrentAccountTransaction> transactions = new ArrayList<>();

    // Status constants
    public static final Integer STATUS_PENDING = 100;
    public static final Integer STATUS_APPROVED = 200;
    public static final Integer STATUS_ACTIVE = 300;
    public static final Integer STATUS_CLOSED = 600;

    // Account type constants
    public static final Integer ACCOUNT_TYPE_INDIVIDUAL = 1;
    public static final Integer ACCOUNT_TYPE_GROUP = 2;

    // Default constructor for JPA
    protected CurrentAccount() {
        // Empty constructor
    }

    // Constructor for creating new accounts
    public CurrentAccount(final Client client, final Group group, final CurrentAccountProduct product,
                          final String accountNumber, final String externalId, final Integer accountType,
                          final LocalDate submittedOnDate, final BigDecimal overdraftLimit) {

        this.client = client;
        this.group = group;
        this.product = product;
        this.accountNumber = accountNumber;
        this.externalId = externalId;
        this.accountType = accountType;
        this.submittedOnDate = submittedOnDate;
        this.overdraftLimit = overdraftLimit != null ? overdraftLimit : BigDecimal.ZERO;

        // Set currency from product
        this.currencyCode = product.getCurrencyCode();
        this.currencyDigits = product.getCurrencyDigits();
        this.inMultiplesOf = product.getInMultiplesOf();

        // Initialize financial fields
        this.accountBalance = BigDecimal.ZERO;
        this.totalDeposits = BigDecimal.ZERO;
        this.totalWithdrawals = BigDecimal.ZERO;

        // Set initial status
        this.status = STATUS_PENDING;
    }

    /**
     * Static factory method to create CurrentAccount from JSON
     */
    public static CurrentAccount fromJson(final Client client, final Group group,
                                          final CurrentAccountProduct product, final JsonCommand command) {

        final String accountNumber = command.stringValueOfParameterNamed("accountNumber");
        final String externalId = command.stringValueOfParameterNamed("externalId");
        final Integer accountType = client != null ? ACCOUNT_TYPE_INDIVIDUAL : ACCOUNT_TYPE_GROUP;
        final LocalDate submittedOnDate = command.localDateValueOfParameterNamed("submittedOnDate");
        final BigDecimal overdraftLimit = command.bigDecimalValueOfParameterNamed("overdraftLimit");

        return new CurrentAccount(client, group, product, accountNumber, externalId,
                accountType, submittedOnDate, overdraftLimit);
    }

    /**
     * Approve the account
     */
    public void approve(final LocalDate approvedOnDate, final Long approvedBy) {
        if (this.status.equals(STATUS_PENDING)) {
            this.status = STATUS_APPROVED;
            this.approvedOnDate = approvedOnDate;
            // approvedBy would be set in the audit fields
        } else {
            throw new CurrentAccountTransactionNotValidException("Account must be in pending status to approve");
        }
    }

    /**
     * Activate the account
     */
    public void activate(final LocalDate activatedOnDate, final Long activatedBy) {
        if (this.status.equals(STATUS_APPROVED)) {
            this.status = STATUS_ACTIVE;
            this.activatedOnDate = activatedOnDate;
            // activatedBy would be set in the audit fields
        } else {
            throw new CurrentAccountTransactionNotValidException("Account must be approved before activation");
        }
    }

    /**
     * Close the account
     */
    public void close(final LocalDate closedOnDate, final Long closedBy) {
        if (this.status.equals(STATUS_ACTIVE)) {
            // Check if balance is zero before closing
            if (this.accountBalance.compareTo(BigDecimal.ZERO) != 0) {
                throw new CurrentAccountTransactionNotValidException("Account balance must be zero before closing");
            }
            this.status = STATUS_CLOSED;
            this.closedOnDate = closedOnDate;
            // closedBy would be set in the audit fields
        } else {
            throw new CurrentAccountTransactionNotValidException("Only active accounts can be closed");
        }
    }

    /**
     * Add a deposit transaction
     */
    public CurrentAccountTransaction deposit(final BigDecimal amount, final LocalDate transactionDate,
                                             final String referenceNumber, final String note) {

        validateTransaction(amount, transactionDate);

        final CurrentAccountTransaction transaction = CurrentAccountTransaction.deposit(this, amount,
                transactionDate, referenceNumber, note);

        this.transactions.add(transaction);
        updateBalanceAfterTransaction(amount, true);

        return transaction;
    }

    /**
     * Add a withdrawal transaction
     */
    public CurrentAccountTransaction withdraw(final BigDecimal amount, final LocalDate transactionDate,
                                              final String referenceNumber, final String note) {

        validateTransaction(amount, transactionDate);
        validateWithdrawal(amount);

        final CurrentAccountTransaction transaction = CurrentAccountTransaction.withdrawal(this, amount,
                transactionDate, referenceNumber, note);

        this.transactions.add(transaction);
        updateBalanceAfterTransaction(amount, false);

        return transaction;
    }

    /**
     * Validate transaction basics
     */
    private void validateTransaction(final BigDecimal amount, final LocalDate transactionDate) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CurrentAccountTransactionNotValidException("Transaction amount must be greater than zero");
        }

        if (transactionDate == null) {
            throw new CurrentAccountTransactionNotValidException("Transaction date cannot be null");
        }

        if (!this.status.equals(STATUS_ACTIVE)) {
            throw new CurrentAccountTransactionNotValidException("Account must be active for transactions");
        }

        // Check daily transaction limit
        if (this.product.getTransactionLimitPerDay() != null) {
            BigDecimal dailyTransactionAmount = calculateDailyTransactionAmount(transactionDate);
            if (dailyTransactionAmount.add(amount).compareTo(this.product.getTransactionLimitPerDay()) > 0) {
                throw new CurrentAccountTransactionNotValidException("Daily transaction limit exceeded");
            }
        }
    }

    /**
     * Validate withdrawal specific rules
     */
    private void validateWithdrawal(final BigDecimal amount) {
        BigDecimal availableBalance = this.accountBalance.add(this.overdraftLimit);
        if (amount.compareTo(availableBalance) > 0) {
            throw new CurrentAccountTransactionNotValidException("Insufficient funds including overdraft limit");
        }
    }

    /**
     * Update account balance after transaction
     */
    private void updateBalanceAfterTransaction(final BigDecimal amount, final boolean isDeposit) {
        if (isDeposit) {
            this.accountBalance = this.accountBalance.add(amount);
            this.totalDeposits = this.totalDeposits.add(amount);
        } else {
            this.accountBalance = this.accountBalance.subtract(amount);
            this.totalWithdrawals = this.totalWithdrawals.add(amount);
        }
    }

    /**
     * Calculate total transaction amount for a specific date
     */
    private BigDecimal calculateDailyTransactionAmount(final LocalDate date) {
        return this.transactions.stream()
                .filter(t -> t.getTransactionDate().equals(date) && !t.isReversed())
                .map(CurrentAccountTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Getter methods
    public String getAccountNumber() {
        return this.accountNumber;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public Client getClient() {
        return this.client;
    }

    public Group getGroup() {
        return this.group;
    }

    public CurrentAccountProduct getProduct() {
        return this.product;
    }

    public Integer getStatus() {
        return this.status;
    }

    public LocalDate getSubmittedOnDate() {
        return this.submittedOnDate;
    }

    public LocalDate getApprovedOnDate() {
        return this.approvedOnDate;
    }

    public LocalDate getActivatedOnDate() {
        return this.activatedOnDate;
    }

    public LocalDate getClosedOnDate() {
        return this.closedOnDate;
    }

    public BigDecimal getAccountBalance() {
        return this.accountBalance;
    }

    public BigDecimal getTotalDeposits() {
        return this.totalDeposits;
    }

    public BigDecimal getTotalWithdrawals() {
        return this.totalWithdrawals;
    }

    public BigDecimal getOverdraftLimit() {
        return this.overdraftLimit;
    }

    public List<CurrentAccountTransaction> getTransactions() {
        return this.transactions;
    }

    /**
     * Get currency as MonetaryCurrency object
     */
    public MonetaryCurrency getCurrency() {
        return new MonetaryCurrency(this.currencyCode, this.currencyDigits, this.inMultiplesOf);
    }

    /**
     * Check if account is active
     */
    public boolean isActive() {
        return this.status.equals(STATUS_ACTIVE);
    }

    /**
     * Check if account is closed
     */
    public boolean isClosed() {
        return this.status.equals(STATUS_CLOSED);
    }

    /**
     * Get available balance (including overdraft)
     */
    public BigDecimal getAvailableBalance() {
        return this.accountBalance.add(this.overdraftLimit);
    }

    /**
     * Update method to modify existing account from JSON command
     * Returns a map of changes made for audit purposes
     */
    public Map<String, Object> update(final JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>(5);

        if (command.isChangeInStringParameterNamed("externalId", this.externalId)) {
            final String newValue = command.stringValueOfParameterNamed("externalId");
            actualChanges.put("externalId", newValue);
            this.externalId = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed("overdraftLimit", this.overdraftLimit)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed("overdraftLimit");
            actualChanges.put("overdraftLimit", newValue);
            this.overdraftLimit = newValue;
        }

        if (command.isChangeInLocalDateParameterNamed("submittedOnDate", this.submittedOnDate)) {
            final LocalDate newValue = command.localDateValueOfParameterNamed("submittedOnDate");
            actualChanges.put("submittedOnDate", newValue);
            this.submittedOnDate = newValue;
        }

        // Optional: Allow manual corrections to lifecycle dates
        if (command.isChangeInLocalDateParameterNamed("approvedOnDate", this.approvedOnDate)) {
            final LocalDate newValue = command.localDateValueOfParameterNamed("approvedOnDate");
            actualChanges.put("approvedOnDate", newValue);
            this.approvedOnDate = newValue;
        }

        if (command.isChangeInLocalDateParameterNamed("activatedOnDate", this.activatedOnDate)) {
            final LocalDate newValue = command.localDateValueOfParameterNamed("activatedOnDate");
            actualChanges.put("activatedOnDate", newValue);
            this.activatedOnDate = newValue;
        }

        if (command.isChangeInLocalDateParameterNamed("closedOnDate", this.closedOnDate)) {
            final LocalDate newValue = command.localDateValueOfParameterNamed("closedOnDate");
            actualChanges.put("closedOnDate", newValue);
            this.closedOnDate = newValue;
        }

        return actualChanges;
    }

}