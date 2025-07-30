// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/handler/UpdateCurrentAccountCommandHandler.java

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
 * Command Handler for updating Current Accounts
 */
@Service
@CommandType(entity = "CURRENTACCOUNT", action = "UPDATE")
public class UpdateCurrentAccountCommandHandler implements NewCommandSourceHandler {

    private final CurrentAccountWritePlatformService writePlatformService;

    @Autowired
    public UpdateCurrentAccountCommandHandler(final CurrentAccountWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.updateCurrentAccount(command.entityId(), command);
    }
}