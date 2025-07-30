// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/service/CurrentAccountWritePlatformService.java

package org.apache.fineract.portfolio.currentaccount.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

/**
 * Interface for writing Current Account data
 */
public interface CurrentAccountWritePlatformService {

    CommandProcessingResult createCurrentAccount(JsonCommand command);

    CommandProcessingResult updateCurrentAccount(Long accountId, JsonCommand command);

    CommandProcessingResult approveCurrentAccount(Long accountId, JsonCommand command);

    CommandProcessingResult activateCurrentAccount(Long accountId, JsonCommand command);

    CommandProcessingResult closeCurrentAccount(Long accountId, JsonCommand command);

    CommandProcessingResult deleteCurrentAccount(Long currentAccountId);

    CommandProcessingResult makeDeposit(Long accountId, JsonCommand command);

    CommandProcessingResult makeWithdrawal(Long accountId, JsonCommand command);
}