// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/service/CurrentAccountWritePlatformServiceImpl.java

package org.apache.fineract.portfolio.currentaccount.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.currentaccount.domain.*;
import org.apache.fineract.portfolio.currentaccount.exception.CurrentAccountNotFoundException;
import org.apache.fineract.portfolio.currentaccount.serialization.CurrentAccountDataValidator;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.group.domain.GroupRepositoryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Implementation of CurrentAccountWritePlatformService
 */
@Service
public class CurrentAccountWritePlatformServiceImpl implements CurrentAccountWritePlatformService {

    private static final Logger LOG = LoggerFactory.getLogger(CurrentAccountWritePlatformServiceImpl.class);

    private final PlatformSecurityContext context;
    private final CurrentAccountRepository currentAccountRepository;
    private final CurrentAccountProductRepository currentAccountProductRepository;
    private final ClientRepositoryWrapper clientRepository;
    private final GroupRepositoryWrapper groupRepository;
    private final CurrentAccountDataValidator currentAccountDataValidator;

    @Autowired
    public CurrentAccountWritePlatformServiceImpl(final PlatformSecurityContext context,
                                                  final CurrentAccountRepository currentAccountRepository,
                                                  final CurrentAccountProductRepository currentAccountProductRepository,
                                                  final ClientRepositoryWrapper clientRepository,
                                                  final GroupRepositoryWrapper groupRepository,
                                                  final CurrentAccountDataValidator currentAccountDataValidator) {
        this.context = context;
        this.currentAccountRepository = currentAccountRepository;
        this.currentAccountProductRepository = currentAccountProductRepository;
        this.clientRepository = clientRepository;
        this.groupRepository = groupRepository;
        this.currentAccountDataValidator = currentAccountDataValidator;
    }

    @Transactional
    @Override
    public CommandProcessingResult createCurrentAccount(final JsonCommand command) {

        try {
            this.context.authenticatedUser();
            this.currentAccountDataValidator.validateForCreate(command.json());

            final Long clientId = command.longValueOfParameterNamed("clientId");
            final Long groupId = command.longValueOfParameterNamed("groupId");
            final Long productId = command.longValueOfParameterNamed("productId");

            Client client = null;
            Group group = null;

            if (clientId != null) {
                client = this.clientRepository.findOneWithNotFoundDetection(clientId);
            }

            if (groupId != null) {
                group = this.groupRepository.findOneWithNotFoundDetection(groupId);
            }

            final CurrentAccountProduct product = this.currentAccountProductRepository.findById(productId)
                    .orElseThrow(() -> new CurrentAccountNotFoundException(productId));

            final CurrentAccount currentAccount = CurrentAccount.fromJson(client, group, product, command);

            this.currentAccountRepository.save(currentAccount);

            return new CommandProcessingResultBuilder()
                    .withCommandId(command.commandId())
                    .withOfficeId(currentAccount.getClient() != null ? currentAccount.getClient().getOffice().getId() : null)
                    .withClientId(clientId)
                    .withGroupId(groupId)
                    .withEntityId((Long) currentAccount.getId())
                    .build();

        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            handleCurrentAccountDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult updateCurrentAccount(final Long currentAccountId, final JsonCommand command) {

        try {
            this.context.authenticatedUser();
            this.currentAccountDataValidator.validateForUpdate(command.json());

            final CurrentAccount currentAccount = this.currentAccountRepository.findById(currentAccountId)
                    .orElseThrow(() -> new CurrentAccountNotFoundException(currentAccountId));

            final Map<String, Object> changes = currentAccount.update(command);

            if (!changes.isEmpty()) {
                this.currentAccountRepository.saveAndFlush(currentAccount);
            }

            return new CommandProcessingResultBuilder()
                    .withCommandId(command.commandId())
                    .withEntityId(currentAccountId)
                    .with(changes)
                    .build();

        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            handleCurrentAccountDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult approveCurrentAccount(final Long currentAccountId, final JsonCommand command) {

        try {
            this.context.authenticatedUser();

            final CurrentAccount currentAccount = this.currentAccountRepository.findById(currentAccountId)
                    .orElseThrow(() -> new CurrentAccountNotFoundException(currentAccountId));

            final LocalDate approvedOnDate = command.localDateValueOfParameterNamed("approvedOnDate");
            final Long approvedBy = this.context.authenticatedUser().getId();

            currentAccount.approve(approvedOnDate, approvedBy);

            this.currentAccountRepository.saveAndFlush(currentAccount);

            return new CommandProcessingResultBuilder()
                    .withCommandId(command.commandId())
                    .withEntityId(currentAccountId)
                    .build();

        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            handleCurrentAccountDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult activateCurrentAccount(final Long currentAccountId, final JsonCommand command) {

        try {
            this.context.authenticatedUser();

            final CurrentAccount currentAccount = this.currentAccountRepository.findById(currentAccountId)
                    .orElseThrow(() -> new CurrentAccountNotFoundException(currentAccountId));

            final LocalDate activatedOnDate = command.localDateValueOfParameterNamed("activatedOnDate");
            final Long activatedBy = this.context.authenticatedUser().getId();

            currentAccount.activate(activatedOnDate, activatedBy);

            this.currentAccountRepository.saveAndFlush(currentAccount);

            return new CommandProcessingResultBuilder()
                    .withCommandId(command.commandId())
                    .withEntityId(currentAccountId)
                    .build();

        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            handleCurrentAccountDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult closeCurrentAccount(final Long currentAccountId, final JsonCommand command) {

        try {
            this.context.authenticatedUser();

            final CurrentAccount currentAccount = this.currentAccountRepository.findById(currentAccountId)
                    .orElseThrow(() -> new CurrentAccountNotFoundException(currentAccountId));

            final LocalDate closedOnDate = command.localDateValueOfParameterNamed("closedOnDate");
            final Long closedBy = this.context.authenticatedUser().getId();

            currentAccount.close(closedOnDate, closedBy);

            this.currentAccountRepository.saveAndFlush(currentAccount);

            return new CommandProcessingResultBuilder()
                    .withCommandId(command.commandId())
                    .withEntityId(currentAccountId)
                    .build();

        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            handleCurrentAccountDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteCurrentAccount(final Long currentAccountId) {

        final CurrentAccount currentAccount = this.currentAccountRepository.findById(currentAccountId)
                .orElseThrow(() -> new CurrentAccountNotFoundException(currentAccountId));

        this.currentAccountRepository.delete(currentAccount);

        return new CommandProcessingResultBuilder()
                .withEntityId(currentAccountId)
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult makeDeposit(final Long currentAccountId, final JsonCommand command) {

        try {
            this.context.authenticatedUser();

            final CurrentAccount currentAccount = this.currentAccountRepository.findById(currentAccountId)
                    .orElseThrow(() -> new CurrentAccountNotFoundException(currentAccountId));

            final BigDecimal amount = command.bigDecimalValueOfParameterNamed("amount");
            final LocalDate transactionDate = command.localDateValueOfParameterNamed("transactionDate");
            final String referenceNumber = command.stringValueOfParameterNamed("referenceNumber");
            final String note = command.stringValueOfParameterNamed("note");

            final CurrentAccountTransaction transaction = currentAccount.deposit(amount, transactionDate, referenceNumber, note);

            this.currentAccountRepository.saveAndFlush(currentAccount);

            return new CommandProcessingResultBuilder()
                    .withCommandId(command.commandId())
                    .withEntityId(currentAccountId)
                    .withSubEntityId((Long) transaction.getId())
                    .build();

        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            handleCurrentAccountDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult makeWithdrawal(final Long currentAccountId, final JsonCommand command) {

        try {
            this.context.authenticatedUser();

            final CurrentAccount currentAccount = this.currentAccountRepository.findById(currentAccountId)
                    .orElseThrow(() -> new CurrentAccountNotFoundException(currentAccountId));

            final BigDecimal amount = command.bigDecimalValueOfParameterNamed("amount");
            final LocalDate transactionDate = command.localDateValueOfParameterNamed("transactionDate");
            final String referenceNumber = command.stringValueOfParameterNamed("referenceNumber");
            final String note = command.stringValueOfParameterNamed("note");

            final CurrentAccountTransaction transaction = currentAccount.withdraw(amount, transactionDate, referenceNumber, note);

            this.currentAccountRepository.saveAndFlush(currentAccount);

            return new CommandProcessingResultBuilder()
                    .withCommandId(command.commandId())
                    .withEntityId(currentAccountId)
                    .withSubEntityId((Long) transaction.getId())
                    .build();

        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            handleCurrentAccountDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        }
    }

    /*
     * Guaranteed to throw an exception no matter what the data integrity issue is.
     */
    private void handleCurrentAccountDataIntegrityIssues(final JsonCommand command, final Throwable realCause,
                                                         final Exception dve) {

        final StringBuilder errorCodeBuilder = new StringBuilder("error.msg.current.account.unknown.data.integrity.issue");
        final String errorCode = errorCodeBuilder.toString();
        String errorMessage = "Unknown data integrity issue with resource.";

        if (realCause.getMessage().contains("external_id_UNIQUE")) {
            errorMessage = "Current account with this external id already exists.";
        } else if (realCause.getMessage().contains("account_no_UNIQUE")) {
            errorMessage = "Current account with this account number already exists.";
        }

        LOG.error("Error occurred.", dve);
        throw new PlatformDataIntegrityException(errorCode, errorMessage, "externalId", "accountNumber");
    }
}