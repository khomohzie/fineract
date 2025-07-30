// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/data/CurrentAccountProductData.java

package org.apache.fineract.portfolio.currentaccount.data;

import org.apache.fineract.organisation.monetary.data.CurrencyData;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Data Transfer Object for Current Account Product
 * This is used to transfer product data between layers (API, Service, etc.)
 * DTOs are immutable - they only contain data, no business logic
 */
public class CurrentAccountProductData {

    // Basic product information
    private final Long id;
    private final String name;
    private final String shortName;
    private final String description;

    // Currency information
    private final CurrencyData currency;

    // Financial parameters
    private final BigDecimal minimumOpeningBalance;
    private final BigDecimal maintenanceFeeAmount;
    private final Integer maintenanceFeeFrequency;
    private final BigDecimal overdraftLimit;
    private final BigDecimal transactionLimitPerDay;
    private final BigDecimal minimumBalanceForInterestCalculation;
    private final BigDecimal interestRate;
    private final Integer interestCalculationType;
    private final Integer interestPostingPeriodType;

    // Status
    private final boolean active;

    // Template data (used when creating/editing products)
    private final Collection<CurrencyData> currencyOptions;

    // Constructor for retrieving existing products
    public CurrentAccountProductData(final Long id, final String name, final String shortName,
                                     final String description, final CurrencyData currency, final BigDecimal minimumOpeningBalance,
                                     final BigDecimal maintenanceFeeAmount, final Integer maintenanceFeeFrequency,
                                     final BigDecimal overdraftLimit, final BigDecimal transactionLimitPerDay,
                                     final BigDecimal minimumBalanceForInterestCalculation, final BigDecimal interestRate,
                                     final Integer interestCalculationType, final Integer interestPostingPeriodType,
                                     final boolean active) {

        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.description = description;
        this.currency = currency;
        this.minimumOpeningBalance = minimumOpeningBalance;
        this.maintenanceFeeAmount = maintenanceFeeAmount;
        this.maintenanceFeeFrequency = maintenanceFeeFrequency;
        this.overdraftLimit = overdraftLimit;
        this.transactionLimitPerDay = transactionLimitPerDay;
        this.minimumBalanceForInterestCalculation = minimumBalanceForInterestCalculation;
        this.interestRate = interestRate;
        this.interestCalculationType = interestCalculationType;
        this.interestPostingPeriodType = interestPostingPeriodType;
        this.active = active;
        this.currencyOptions = null;
    }

    // Constructor for template data (when creating new products)
    public CurrentAccountProductData(final Collection<CurrencyData> currencyOptions) {
        this.id = null;
        this.name = null;
        this.shortName = null;
        this.description = null;
        this.currency = null;
        this.minimumOpeningBalance = null;
        this.maintenanceFeeAmount = null;
        this.maintenanceFeeFrequency = null;
        this.overdraftLimit = null;
        this.transactionLimitPerDay = null;
        this.minimumBalanceForInterestCalculation = null;
        this.interestRate = null;
        this.interestCalculationType = null;
        this.interestPostingPeriodType = null;
        this.active = true;
        this.currencyOptions = currencyOptions;
    }

    // Static factory method to create template
    public static CurrentAccountProductData template(final Collection<CurrencyData> currencyOptions) {
        return new CurrentAccountProductData(currencyOptions);
    }

    // Getter methods
    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getShortName() {
        return this.shortName;
    }

    public String getDescription() {
        return this.description;
    }

    public CurrencyData getCurrency() {
        return this.currency;
    }

    public BigDecimal getMinimumOpeningBalance() {
        return this.minimumOpeningBalance;
    }

    public BigDecimal getMaintenanceFeeAmount() {
        return this.maintenanceFeeAmount;
    }

    public Integer getMaintenanceFeeFrequency() {
        return this.maintenanceFeeFrequency;
    }

    public BigDecimal getOverdraftLimit() {
        return this.overdraftLimit;
    }

    public BigDecimal getTransactionLimitPerDay() {
        return this.transactionLimitPerDay;
    }

    public BigDecimal getMinimumBalanceForInterestCalculation() {
        return this.minimumBalanceForInterestCalculation;
    }

    public BigDecimal getInterestRate() {
        return this.interestRate;
    }

    public Integer getInterestCalculationType() {
        return this.interestCalculationType;
    }

    public Integer getInterestPostingPeriodType() {
        return this.interestPostingPeriodType;
    }

    public boolean isActive() {
        return this.active;
    }

    public Collection<CurrencyData> getCurrencyOptions() {
        return this.currencyOptions;
    }
}