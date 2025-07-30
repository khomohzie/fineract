// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/handler/CurrentAccountWithdrawalCommandHandler.java

package org.apache.fineract.portfolio.currentaccount.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.currentaccount.service.CurrentAccountWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Command Handler for Current Account Withdrawals
 */
@Service
@CommandType(entity = "CURRENTACCOUNT", action = "WITHDRAWAL")
public class CurrentAccountWithdrawalCommandHandler implements NewCommandSourceHandler {

    private final CurrentAccountWritePlatformService writePlatformService;

    @Autowired
    public CurrentAccountWithdrawalCommandHandler(final CurrentAccountWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.makeWithdrawal(command.entityId(), command);
    }
}