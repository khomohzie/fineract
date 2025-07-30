// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/serialization/CurrentAccountProductDataValidator.java

package org.apache.fineract.portfolio.currentaccount.serialization;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

/**
 * Validator for Current Account Product JSON data
 * This class validates all input JSON for current account product operations
 */
@Component
public class CurrentAccountProductDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    // Define all supported parameters for current account products
    private static final Set<String> CURRENT_ACCOUNT_PRODUCT_CREATE_REQUEST_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList("name", "shortName", "description", "currencyCode", "currencyDigits", "inMultiplesOf",
                    "minimumOpeningBalance", "maintenanceFeeAmount", "maintenanceFeeFrequency", "overdraftLimit",
                    "transactionLimitPerDay", "minimumBalanceForInterestCalculation", "interestRate",
                    "interestCalculationType", "interestPostingPeriodType", "locale"));

    private static final Set<String> CURRENT_ACCOUNT_PRODUCT_UPDATE_REQUEST_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList("name", "shortName", "description", "minimumOpeningBalance", "maintenanceFeeAmount",
                    "maintenanceFeeFrequency", "overdraftLimit", "transactionLimitPerDay",
                    "minimumBalanceForInterestCalculation", "interestRate", "interestCalculationType",
                    "interestPostingPeriodType", "locale"));

    @Autowired
    public CurrentAccountProductDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    /**
     * Create a mutable locale set for JSON parsing
     */
    private Set<String> createLocaleSet() {
        final Set<String> locales = new HashSet<>();
        locales.add("en");
        return locales;
    }

    /**
     * Validate JSON data for creating a new current account product
     */
    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {
        }.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                CURRENT_ACCOUNT_PRODUCT_CREATE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource("currentaccountproduct");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        // Validate required fields
        final String name = this.fromApiJsonHelper.extractStringNamed("name", element);
        baseDataValidator.reset().parameter("name").value(name).notBlank().notExceedingLengthOf(100);

        final String shortName = this.fromApiJsonHelper.extractStringNamed("shortName", element);
        baseDataValidator.reset().parameter("shortName").value(shortName).notBlank().notExceedingLengthOf(4);

        final String description = this.fromApiJsonHelper.extractStringNamed("description", element);
        baseDataValidator.reset().parameter("description").value(description).notExceedingLengthOf(500);

        // Validate currency
        final String currencyCode = this.fromApiJsonHelper.extractStringNamed("currencyCode", element);
        baseDataValidator.reset().parameter("currencyCode").value(currencyCode).notBlank().notExceedingLengthOf(3);

        if (this.fromApiJsonHelper.parameterExists("currencyDigits", element)) {
            final Integer currencyDigits = this.fromApiJsonHelper.extractIntegerNamed("currencyDigits", element, createLocaleSet());
            baseDataValidator.reset().parameter("currencyDigits").value(currencyDigits).notNull()
                    .integerZeroOrGreater().inMinMaxRange(0, 6);
        }

        if (this.fromApiJsonHelper.parameterExists("inMultiplesOf", element)) {
            final Integer inMultiplesOf = this.fromApiJsonHelper.extractIntegerNamed("inMultiplesOf", element, createLocaleSet());
            baseDataValidator.reset().parameter("inMultiplesOf").value(inMultiplesOf)
                    .integerGreaterThanZero();
        }

        // Validate financial parameters
        if (this.fromApiJsonHelper.parameterExists("minimumOpeningBalance", element)) {
            final BigDecimal minimumOpeningBalance = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed("minimumOpeningBalance", element);
            baseDataValidator.reset().parameter("minimumOpeningBalance").value(minimumOpeningBalance)
                    .positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists("maintenanceFeeAmount", element)) {
            final BigDecimal maintenanceFeeAmount = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed("maintenanceFeeAmount", element);
            baseDataValidator.reset().parameter("maintenanceFeeAmount").value(maintenanceFeeAmount)
                    .zeroOrPositiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists("maintenanceFeeFrequency", element)) {
            final Integer maintenanceFeeFrequency = this.fromApiJsonHelper
                    .extractIntegerNamed("maintenanceFeeFrequency", element, createLocaleSet());
            baseDataValidator.reset().parameter("maintenanceFeeFrequency").value(maintenanceFeeFrequency)
                    .isOneOfTheseValues(1, 2, 3); // 1=Monthly, 2=Quarterly, 3=Annually
        }

        if (this.fromApiJsonHelper.parameterExists("overdraftLimit", element)) {
            final BigDecimal overdraftLimit = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed("overdraftLimit", element);
            baseDataValidator.reset().parameter("overdraftLimit").value(overdraftLimit)
                    .zeroOrPositiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists("transactionLimitPerDay", element)) {
            final BigDecimal transactionLimitPerDay = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed("transactionLimitPerDay", element);
            baseDataValidator.reset().parameter("transactionLimitPerDay").value(transactionLimitPerDay)
                    .positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists("minimumBalanceForInterestCalculation", element)) {
            final BigDecimal minimumBalanceForInterestCalculation = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed("minimumBalanceForInterestCalculation", element);
            baseDataValidator.reset().parameter("minimumBalanceForInterestCalculation")
                    .value(minimumBalanceForInterestCalculation).positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists("interestRate", element)) {
            final BigDecimal interestRate = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed("interestRate", element);
            baseDataValidator.reset().parameter("interestRate").value(interestRate)
                    .zeroOrPositiveAmount().notGreaterThanMax(BigDecimal.valueOf(100));
        }

        if (this.fromApiJsonHelper.parameterExists("interestCalculationType", element)) {
            final Integer interestCalculationType = this.fromApiJsonHelper
                    .extractIntegerNamed("interestCalculationType", element, createLocaleSet());
            baseDataValidator.reset().parameter("interestCalculationType").value(interestCalculationType)
                    .isOneOfTheseValues(1, 2); // 1=Daily Balance, 2=Average Daily Balance
        }

        if (this.fromApiJsonHelper.parameterExists("interestPostingPeriodType", element)) {
            final Integer interestPostingPeriodType = this.fromApiJsonHelper
                    .extractIntegerNamed("interestPostingPeriodType", element, createLocaleSet());
            baseDataValidator.reset().parameter("interestPostingPeriodType").value(interestPostingPeriodType)
                    .isOneOfTheseValues(1, 2, 3, 4); // 1=Daily, 2=Weekly, 3=Monthly, 4=Quarterly
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    /**
     * Validate JSON data for updating an existing current account product
     */
    public void validateForUpdate(final String json) {

        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {
        }.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                CURRENT_ACCOUNT_PRODUCT_UPDATE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource("currentaccountproduct");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        // For updates, fields are optional but if provided, they must be valid
        if (this.fromApiJsonHelper.parameterExists("name", element)) {
            final String name = this.fromApiJsonHelper.extractStringNamed("name", element);
            baseDataValidator.reset().parameter("name").value(name).notBlank().notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.parameterExists("shortName", element)) {
            final String shortName = this.fromApiJsonHelper.extractStringNamed("shortName", element);
            baseDataValidator.reset().parameter("shortName").value(shortName).notBlank().notExceedingLengthOf(4);
        }

        if (this.fromApiJsonHelper.parameterExists("description", element)) {
            final String description = this.fromApiJsonHelper.extractStringNamed("description", element);
            baseDataValidator.reset().parameter("description").value(description).notExceedingLengthOf(500);
        }

        if (this.fromApiJsonHelper.parameterExists("minimumOpeningBalance", element)) {
            final BigDecimal minimumOpeningBalance = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed("minimumOpeningBalance", element);
            baseDataValidator.reset().parameter("minimumOpeningBalance").value(minimumOpeningBalance)
                    .positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists("maintenanceFeeAmount", element)) {
            final BigDecimal maintenanceFeeAmount = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed("maintenanceFeeAmount", element);
            baseDataValidator.reset().parameter("maintenanceFeeAmount").value(maintenanceFeeAmount)
                    .zeroOrPositiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists("maintenanceFeeFrequency", element)) {
            final Integer maintenanceFeeFrequency = this.fromApiJsonHelper
                    .extractIntegerNamed("maintenanceFeeFrequency", element, createLocaleSet());
            baseDataValidator.reset().parameter("maintenanceFeeFrequency").value(maintenanceFeeFrequency)
                    .isOneOfTheseValues(1, 2, 3);
        }

        if (this.fromApiJsonHelper.parameterExists("overdraftLimit", element)) {
            final BigDecimal overdraftLimit = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed("overdraftLimit", element);
            baseDataValidator.reset().parameter("overdraftLimit").value(overdraftLimit)
                    .zeroOrPositiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists("transactionLimitPerDay", element)) {
            final BigDecimal transactionLimitPerDay = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed("transactionLimitPerDay", element);
            baseDataValidator.reset().parameter("transactionLimitPerDay").value(transactionLimitPerDay)
                    .positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists("minimumBalanceForInterestCalculation", element)) {
            final BigDecimal minimumBalanceForInterestCalculation = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed("minimumBalanceForInterestCalculation", element);
            baseDataValidator.reset().parameter("minimumBalanceForInterestCalculation")
                    .value(minimumBalanceForInterestCalculation).positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists("interestRate", element)) {
            final BigDecimal interestRate = this.fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed("interestRate", element);
            baseDataValidator.reset().parameter("interestRate").value(interestRate)
                    .zeroOrPositiveAmount().notGreaterThanMax(BigDecimal.valueOf(100));
        }

        if (this.fromApiJsonHelper.parameterExists("interestCalculationType", element)) {
            final Integer interestCalculationType = this.fromApiJsonHelper
                    .extractIntegerNamed("interestCalculationType", element, createLocaleSet());
            baseDataValidator.reset().parameter("interestCalculationType").value(interestCalculationType)
                    .isOneOfTheseValues(1, 2);
        }

        if (this.fromApiJsonHelper.parameterExists("interestPostingPeriodType", element)) {
            final Integer interestPostingPeriodType = this.fromApiJsonHelper
                    .extractIntegerNamed("interestPostingPeriodType", element, createLocaleSet());
            baseDataValidator.reset().parameter("interestPostingPeriodType").value(interestPostingPeriodType)
                    .isOneOfTheseValues(1, 2, 3, 4);
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    /**
     * Throw exception if there are validation errors
     */
    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
    }
}