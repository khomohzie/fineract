// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/domain/CurrentAccountProduct.java

package org.apache.fineract.portfolio.currentaccount.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Entity class representing a Current Account Product configuration
 * This defines the template/rules for creating current account instances
 */
@Entity
@Table(name = "m_current_account_product")
public class CurrentAccountProduct extends AbstractPersistableCustom<Long> {

    // Basic product information
    @Getter
    @Column(name = "name", nullable = false)
    private String name;

    @Getter
    @Column(name = "short_name", nullable = false)
    private String shortName;

    @Getter
    @Column(name = "description")
    private String description;

    // Currency configuration
    @Getter
    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @Getter
    @Column(name = "currency_digits", nullable = false)
    private Integer currencyDigits;

    @Getter
    @Column(name = "currency_multiplesof")
    private Integer inMultiplesOf;

    // Financial parameters
    @Getter
    @Column(name = "minimum_opening_balance", scale = 6, precision = 19)
    private BigDecimal minimumOpeningBalance;

    @Getter
    @Column(name = "maintenance_fee_amount", scale = 6, precision = 19)
    private BigDecimal maintenanceFeeAmount;

    @Column(name = "maintenance_fee_frequency")
    private Integer maintenanceFeeFrequency;

    @Getter
    @Column(name = "overdraft_limit", scale = 6, precision = 19)
    private BigDecimal overdraftLimit;

    @Getter
    @Column(name = "transaction_limit_per_day", scale = 6, precision = 19)
    private BigDecimal transactionLimitPerDay;

    @Column(name = "minimum_balance_for_interest_calculation", scale = 6, precision = 19)
    private BigDecimal minimumBalanceForInterestCalculation;

    @Getter
    @Column(name = "interest_rate", scale = 6, precision = 19)
    private BigDecimal interestRate;

    @Column(name = "interest_calculation_type")
    private Integer interestCalculationType;

    @Column(name = "interest_posting_period_type")
    private Integer interestPostingPeriodType;

    // Status and audit fields
    @Getter
    @Column(name = "is_active")
    private boolean active;

    // Default constructor required by JPA
    protected CurrentAccountProduct() {
        // Empty constructor for JPA
    }

    // Constructor for creating new products
    public CurrentAccountProduct(final String name, final String shortName, final String description,
                                 final String currencyCode, final Integer currencyDigits, final Integer inMultiplesOf,
                                 final BigDecimal minimumOpeningBalance, final BigDecimal maintenanceFeeAmount,
                                 final Integer maintenanceFeeFrequency, final BigDecimal overdraftLimit,
                                 final BigDecimal transactionLimitPerDay, final BigDecimal minimumBalanceForInterestCalculation,
                                 final BigDecimal interestRate, final Integer interestCalculationType,
                                 final Integer interestPostingPeriodType) {

        this.name = name;
        this.shortName = shortName;
        this.description = description;
        this.currencyCode = currencyCode;
        this.currencyDigits = currencyDigits;
        this.inMultiplesOf = inMultiplesOf;
        this.minimumOpeningBalance = minimumOpeningBalance;
        this.maintenanceFeeAmount = maintenanceFeeAmount;
        this.maintenanceFeeFrequency = maintenanceFeeFrequency;
        this.overdraftLimit = overdraftLimit;
        this.transactionLimitPerDay = transactionLimitPerDay;
        this.minimumBalanceForInterestCalculation = minimumBalanceForInterestCalculation;
        this.interestRate = interestRate;
        this.interestCalculationType = interestCalculationType;
        this.interestPostingPeriodType = interestPostingPeriodType;
        this.active = true; // New products are active by default
    }

    /**
     * Static factory method to create a new CurrentAccountProduct from JSON command
     * This is used when creating a product via the REST API
     */
    public static CurrentAccountProduct fromJson(final JsonCommand command) {
        final String name = command.stringValueOfParameterNamed("name");
        final String shortName = command.stringValueOfParameterNamed("shortName");
        final String description = command.stringValueOfParameterNamed("description");
        final String currencyCode = command.stringValueOfParameterNamed("currencyCode");
        final Integer currencyDigits = command.integerValueOfParameterNamed("currencyDigits");
        final Integer inMultiplesOf = command.integerValueOfParameterNamed("inMultiplesOf");
        final BigDecimal minimumOpeningBalance = command.bigDecimalValueOfParameterNamed("minimumOpeningBalance");
        final BigDecimal maintenanceFeeAmount = command.bigDecimalValueOfParameterNamed("maintenanceFeeAmount");
        final Integer maintenanceFeeFrequency = command.integerValueOfParameterNamed("maintenanceFeeFrequency");
        final BigDecimal overdraftLimit = command.bigDecimalValueOfParameterNamed("overdraftLimit");
        final BigDecimal transactionLimitPerDay = command.bigDecimalValueOfParameterNamed("transactionLimitPerDay");
        final BigDecimal minimumBalanceForInterestCalculation = command.bigDecimalValueOfParameterNamed("minimumBalanceForInterestCalculation");
        final BigDecimal interestRate = command.bigDecimalValueOfParameterNamed("interestRate");
        final Integer interestCalculationType = command.integerValueOfParameterNamed("interestCalculationType");
        final Integer interestPostingPeriodType = command.integerValueOfParameterNamed("interestPostingPeriodType");

        return new CurrentAccountProduct(name, shortName, description, currencyCode, currencyDigits,
                inMultiplesOf, minimumOpeningBalance, maintenanceFeeAmount, maintenanceFeeFrequency,
                overdraftLimit, transactionLimitPerDay, minimumBalanceForInterestCalculation,
                interestRate, interestCalculationType, interestPostingPeriodType);
    }

    /**
     * Update method to modify existing product from JSON command
     * Returns a map of changes made for audit purposes
     */
    public Map<String, Object> update(final JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>(10);

        if (command.isChangeInStringParameterNamed("name", this.name)) {
            final String newValue = command.stringValueOfParameterNamed("name");
            actualChanges.put("name", newValue);
            this.name = newValue;
        }

        if (command.isChangeInStringParameterNamed("shortName", this.shortName)) {
            final String newValue = command.stringValueOfParameterNamed("shortName");
            actualChanges.put("shortName", newValue);
            this.shortName = newValue;
        }

        if (command.isChangeInStringParameterNamed("description", this.description)) {
            final String newValue = command.stringValueOfParameterNamed("description");
            actualChanges.put("description", newValue);
            this.description = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed("minimumOpeningBalance", this.minimumOpeningBalance)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed("minimumOpeningBalance");
            actualChanges.put("minimumOpeningBalance", newValue);
            this.minimumOpeningBalance = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed("maintenanceFeeAmount", this.maintenanceFeeAmount)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed("maintenanceFeeAmount");
            actualChanges.put("maintenanceFeeAmount", newValue);
            this.maintenanceFeeAmount = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed("overdraftLimit", this.overdraftLimit)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed("overdraftLimit");
            actualChanges.put("overdraftLimit", newValue);
            this.overdraftLimit = newValue;
        }

        // Add more update checks for other fields as needed...

        return actualChanges;
    }

    // Getter methods


    /**
     * Get currency as MonetaryCurrency object
     * This is used throughout Fineract for currency operations
     */
    public MonetaryCurrency getCurrency() {
        return new MonetaryCurrency(this.currencyCode, this.currencyDigits, this.inMultiplesOf);
    }

    /**
     * Activate the product
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Deactivate the product
     */
    public void deactivate() {
        this.active = false;
    }

}