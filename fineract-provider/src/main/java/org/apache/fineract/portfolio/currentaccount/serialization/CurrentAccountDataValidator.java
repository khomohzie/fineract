// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/serialization/CurrentAccountDataValidator.java

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
import java.time.LocalDate;
import java.util.*;

/**
 * Validator for Current Account JSON data
 */
@Component
public class CurrentAccountDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    // Define all supported parameters for current account operations
    private static final Set<String> CURRENT_ACCOUNT_CREATE_REQUEST_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList("clientId", "groupId", "productId", "accountNumber", "externalId", "submittedOnDate",
                    "overdraftLimit", "locale", "dateFormat"));

    private static final Set<String> CURRENT_ACCOUNT_UPDATE_REQUEST_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList("accountNumber", "externalId", "overdraftLimit"));

    private static final Set<String> CURRENT_ACCOUNT_APPROVE_REQUEST_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList("approvedOnDate", "locale", "dateFormat"));

    private static final Set<String> CURRENT_ACCOUNT_ACTIVATE_REQUEST_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList("activatedOnDate", "locale", "dateFormat"));

    private static final Set<String> CURRENT_ACCOUNT_CLOSE_REQUEST_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList("closedOnDate", "locale", "dateFormat"));

    private static final Set<String> CURRENT_ACCOUNT_TRANSACTION_REQUEST_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList("amount", "transactionDate", "referenceNumber", "note", "locale", "dateFormat"));

    @Autowired
    public CurrentAccountDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    /**
     * Validate JSON data for creating a new current account
     */
    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {
        }.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                CURRENT_ACCOUNT_CREATE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource("currentaccount");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        // Validate required fields
        final Long clientId = this.fromApiJsonHelper.extractLongNamed("clientId", element);
        final Long groupId = this.fromApiJsonHelper.extractLongNamed("groupId", element);

        if (clientId == null && groupId == null) {
            baseDataValidator.reset().parameter("clientId").failWithCode("current.account.must.have.client.or.group");
        }

        if (clientId != null && groupId != null) {
            baseDataValidator.reset().parameter("clientId").failWithCode("current.account.cannot.have.both.client.and.group");
        }

        final Long productId = this.fromApiJsonHelper.extractLongNamed("productId", element);
        baseDataValidator.reset().parameter("productId").value(productId).notNull().longGreaterThanZero();

        final String accountNumber = this.fromApiJsonHelper.extractStringNamed("accountNumber", element);
        baseDataValidator.reset().parameter("accountNumber").value(accountNumber).notBlank().notExceedingLengthOf(20);

        final String externalId = this.fromApiJsonHelper.extractStringNamed("externalId", element);
        if (StringUtils.isNotBlank(externalId)) {
            baseDataValidator.reset().parameter("externalId").value(externalId).notExceedingLengthOf(100);
        }

        final LocalDate submittedOnDate = this.fromApiJsonHelper.extractLocalDateNamed("submittedOnDate", element);
        baseDataValidator.reset().parameter("submittedOnDate").value(submittedOnDate).notNull();

        if (this.fromApiJsonHelper.parameterExists("overdraftLimit", element)) {
            final BigDecimal overdraftLimit = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("overdraftLimit", element);
            baseDataValidator.reset().parameter("overdraftLimit").value(overdraftLimit).zeroOrPositiveAmount();
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    /**
     * Validate JSON data for updating an existing current account
     */
    public void validateForUpdate(final String json) {

        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {
        }.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                CURRENT_ACCOUNT_UPDATE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource("currentaccount");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        // For updates, fields are optional but if provided, they must be valid
        if (this.fromApiJsonHelper.parameterExists("accountNumber", element)) {
            final String accountNumber = this.fromApiJsonHelper.extractStringNamed("accountNumber", element);
            baseDataValidator.reset().parameter("accountNumber").value(accountNumber).notBlank().notExceedingLengthOf(20);
        }

        if (this.fromApiJsonHelper.parameterExists("externalId", element)) {
            final String externalId = this.fromApiJsonHelper.extractStringNamed("externalId", element);
            baseDataValidator.reset().parameter("externalId").value(externalId).notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.parameterExists("overdraftLimit", element)) {
            final BigDecimal overdraftLimit = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("overdraftLimit", element);
            baseDataValidator.reset().parameter("overdraftLimit").value(overdraftLimit).zeroOrPositiveAmount();
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    /**
     * Validate JSON data for approving a current account
     */
    public void validateForApprove(final String json) {

        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {
        }.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                CURRENT_ACCOUNT_APPROVE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource("currentaccount");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final LocalDate approvedOnDate = this.fromApiJsonHelper.extractLocalDateNamed("approvedOnDate", element);
        baseDataValidator.reset().parameter("approvedOnDate").value(approvedOnDate).notNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    /**
     * Validate JSON data for activating a current account
     */
    public void validateForActivate(final String json) {

        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {
        }.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                CURRENT_ACCOUNT_ACTIVATE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource("currentaccount");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final LocalDate activatedOnDate = this.fromApiJsonHelper.extractLocalDateNamed("activatedOnDate", element);
        baseDataValidator.reset().parameter("activatedOnDate").value(activatedOnDate).notNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    /**
     * Validate JSON data for closing a current account
     */
    public void validateForClose(final String json) {

        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {
        }.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                CURRENT_ACCOUNT_CLOSE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource("currentaccount");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final LocalDate closedOnDate = this.fromApiJsonHelper.extractLocalDateNamed("closedOnDate", element);
        baseDataValidator.reset().parameter("closedOnDate").value(closedOnDate).notNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    /**
     * Validate JSON data for current account transactions
     */
    public void validateForTransaction(final String json) {

        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {
        }.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                CURRENT_ACCOUNT_TRANSACTION_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource("currentaccounttransaction");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("amount", element);
        baseDataValidator.reset().parameter("amount").value(amount).notNull().positiveAmount();

        final LocalDate transactionDate = this.fromApiJsonHelper.extractLocalDateNamed("transactionDate", element);
        baseDataValidator.reset().parameter("transactionDate").value(transactionDate).notNull();

        final String referenceNumber = this.fromApiJsonHelper.extractStringNamed("referenceNumber", element);
        if (StringUtils.isNotBlank(referenceNumber)) {
            baseDataValidator.reset().parameter("referenceNumber").value(referenceNumber).notExceedingLengthOf(50);
        }

        final String note = this.fromApiJsonHelper.extractStringNamed("note", element);
        if (StringUtils.isNotBlank(note)) {
            baseDataValidator.reset().parameter("note").value(note).notExceedingLengthOf(1000);
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