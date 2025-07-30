// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/data/CurrentAccountData.java

package org.apache.fineract.portfolio.currentaccount.data;

import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.group.data.GroupGeneralData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;

/**
 * Data Transfer Object for Current Account
 */
public class CurrentAccountData {

    // Basic account information
    private final Long id;
    private final String accountNumber;
    private final String externalId;

    // Relationships
    private final Long clientId;
    private final String clientName;
    private final Long groupId;
    private final String groupName;
    private final Long productId;
    private final String productName;

    // Account status and dates
    private final Integer status;
    private final String statusName;
    private final LocalDate submittedOnDate;
    private final LocalDate approvedOnDate;
    private final LocalDate activatedOnDate;
    private final LocalDate closedOnDate;

    // Currency and financial information
    private final CurrencyData currency;
    private final BigDecimal accountBalance;
    private final BigDecimal totalDeposits;
    private final BigDecimal totalWithdrawals;
    private final BigDecimal overdraftLimit;
    private final BigDecimal availableBalance;

    // Template data (for creating/editing accounts)
    private final Collection<ClientData> clientOptions;
    private final Collection<GroupGeneralData> groupOptions;
    private final Collection<CurrentAccountProductData> productOptions;

    // Constructor for existing accounts
    public CurrentAccountData(final Long id, final String accountNumber, final String externalId,
                              final Long clientId, final String clientName, final Long groupId, final String groupName,
                              final Long productId, final String productName, final Integer status, final String statusName,
                              final LocalDate submittedOnDate, final LocalDate approvedOnDate, final LocalDate activatedOnDate,
                              final LocalDate closedOnDate, final CurrencyData currency, final BigDecimal accountBalance,
                              final BigDecimal totalDeposits, final BigDecimal totalWithdrawals, final BigDecimal overdraftLimit,
                              final BigDecimal availableBalance) {

        this.id = id;
        this.accountNumber = accountNumber;
        this.externalId = externalId;
        this.clientId = clientId;
        this.clientName = clientName;
        this.groupId = groupId;
        this.groupName = groupName;
        this.productId = productId;
        this.productName = productName;
        this.status = status;
        this.statusName = statusName;
        this.submittedOnDate = submittedOnDate;
        this.approvedOnDate = approvedOnDate;
        this.activatedOnDate = activatedOnDate;
        this.closedOnDate = closedOnDate;
        this.currency = currency;
        this.accountBalance = accountBalance;
        this.totalDeposits = totalDeposits;
        this.totalWithdrawals = totalWithdrawals;
        this.overdraftLimit = overdraftLimit;
        this.availableBalance = availableBalance;
        this.clientOptions = null;
        this.groupOptions = null;
        this.productOptions = null;
    }

    // Constructor for template data
    public CurrentAccountData(final Collection<ClientData> clientOptions,
                              final Collection<GroupGeneralData> groupOptions,
                              final Collection<CurrentAccountProductData> productOptions) {

        this.id = null;
        this.accountNumber = null;
        this.externalId = null;
        this.clientId = null;
        this.clientName = null;
        this.groupId = null;
        this.groupName = null;
        this.productId = null;
        this.productName = null;
        this.status = null;
        this.statusName = null;
        this.submittedOnDate = null;
        this.approvedOnDate = null;
        this.activatedOnDate = null;
        this.closedOnDate = null;
        this.currency = null;
        this.accountBalance = null;
        this.totalDeposits = null;
        this.totalWithdrawals = null;
        this.overdraftLimit = null;
        this.availableBalance = null;
        this.clientOptions = clientOptions;
        this.groupOptions = groupOptions;
        this.productOptions = productOptions;
    }

    // Static factory method for template
    public static CurrentAccountData template(final Collection<ClientData> clientOptions,
                                              final Collection<GroupGeneralData> groupOptions,
                                              final Collection<CurrentAccountProductData> productOptions) {
        return new CurrentAccountData(clientOptions, groupOptions, productOptions);
    }

    // Getter methods
    public Long getId() {
        return this.id;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public Long getClientId() {
        return this.clientId;
    }

    public String getClientName() {
        return this.clientName;
    }

    public Long getGroupId() {
        return this.groupId;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public Long getProductId() {
        return this.productId;
    }

    public String getProductName() {
        return this.productName;
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getStatusName() {
        return this.statusName;
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

    public CurrencyData getCurrency() {
        return this.currency;
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

    public BigDecimal getAvailableBalance() {
        return this.availableBalance;
    }

    public Collection<ClientData> getClientOptions() {
        return this.clientOptions;
    }

    public Collection<GroupGeneralData> getGroupOptions() {
        return this.groupOptions;
    }

    public Collection<CurrentAccountProductData> getProductOptions() {
        return this.productOptions;
    }
}