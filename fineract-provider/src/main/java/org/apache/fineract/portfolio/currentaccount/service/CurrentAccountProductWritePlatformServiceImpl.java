// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/service/CurrentAccountProductWritePlatformServiceImpl.java

package org.apache.fineract.portfolio.currentaccount.service;

import jakarta.persistence.PersistenceException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.currentaccount.domain.CurrentAccountProduct;
import org.apache.fineract.portfolio.currentaccount.domain.CurrentAccountProductRepository;
import org.apache.fineract.portfolio.currentaccount.exception.CurrentAccountProductNotFoundException;
import org.apache.fineract.portfolio.currentaccount.serialization.CurrentAccountProductDataValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Implementation of CurrentAccountProductWritePlatformService
 */
@Service
public class CurrentAccountProductWritePlatformServiceImpl implements CurrentAccountProductWritePlatformService {

    private static final Logger LOG = LoggerFactory.getLogger(CurrentAccountProductWritePlatformServiceImpl.class);

    private final PlatformSecurityContext context;
    private final CurrentAccountProductRepository currentAccountProductRepository;
    private final CurrentAccountProductDataValidator dataValidator;

    @Autowired
    public CurrentAccountProductWritePlatformServiceImpl(final PlatformSecurityContext context,
                                                         final CurrentAccountProductRepository currentAccountProductRepository,
                                                         final CurrentAccountProductDataValidator dataValidator) {
        this.context = context;
        this.currentAccountProductRepository = currentAccountProductRepository;
        this.dataValidator = dataValidator;
    }

    @Transactional
    @Override
    public CommandProcessingResult createCurrentAccountProduct(final JsonCommand command) {

        try {
            this.context.authenticatedUser();

            // Validate the input data
            this.dataValidator.validateForCreate(command.json());

            // Create the product from JSON
            final CurrentAccountProduct product = CurrentAccountProduct.fromJson(command);

            // Save to database
            this.currentAccountProductRepository.saveAndFlush(product);

            return new CommandProcessingResultBuilder()
                    .withCommandId(command.commandId())
                    .withEntityId(product.getId())
                    .build();

        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        } catch (final PersistenceException dve) {
            Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
            handleDataIntegrityIssues(command, throwable, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult updateCurrentAccountProduct(final Long productId, final JsonCommand command) {

        try {
            this.context.authenticatedUser();

            // Validate the input data
            this.dataValidator.validateForUpdate(command.json());

            // Find the existing product
            final CurrentAccountProduct product = this.currentAccountProductRepository.findById(productId)
                    .orElseThrow(() -> new CurrentAccountProductNotFoundException(productId));

            // Update the product
            final Map<String, Object> changes = product.update(command);

            // Save if there are changes
            if (!changes.isEmpty()) {
                this.currentAccountProductRepository.saveAndFlush(product);
            }

            return new CommandProcessingResultBuilder()
                    .withCommandId(command.commandId())
                    .withEntityId(productId)
                    .with(changes)
                    .build();

        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        } catch (final PersistenceException dve) {
            Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
            handleDataIntegrityIssues(command, throwable, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteCurrentAccountProduct(final Long productId) {

        this.context.authenticatedUser();

        final CurrentAccountProduct product = this.currentAccountProductRepository.findById(productId)
                .orElseThrow(() -> new CurrentAccountProductNotFoundException(productId));

        // Check if product is being used by any accounts
        // In a real implementation, you'd check if any accounts are using this product

        this.currentAccountProductRepository.delete(product);

        return new CommandProcessingResultBuilder()
                .withEntityId(productId)
                .build();
    }

    /**
     * Handle database constraint violations and convert them to meaningful exceptions
     */
    private void handleDataIntegrityIssues(final JsonCommand command, final Throwable realCause, final Exception dve) {

        if (realCause.getMessage().contains("current_account_product_name_UNIQUE")) {
            final String name = command.stringValueOfParameterNamed("name");
            throw new PlatformDataIntegrityException("error.msg.currentaccountproduct.duplicate.name",
                    "Current account product with name `" + name + "` already exists", "name", name);
        } else if (realCause.getMessage().contains("current_account_product_short_name_UNIQUE")) {
            final String shortName = command.stringValueOfParameterNamed("shortName");
            throw new PlatformDataIntegrityException("error.msg.currentaccountproduct.duplicate.shortname",
                    "Current account product with short name `" + shortName + "` already exists", "shortName", shortName);
        }

        LOG.error("Error occurred.", dve);
        throw new PlatformDataIntegrityException("error.msg.currentaccountproduct.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }
}