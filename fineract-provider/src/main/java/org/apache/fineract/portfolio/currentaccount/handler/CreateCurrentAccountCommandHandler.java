// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/handler/CreateCurrentAccountCommandHandler.java

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
 * Command Handler for creating Current Accounts
 */
@Service
@CommandType(entity = "CURRENTACCOUNT", action = "CREATE")
public class CreateCurrentAccountCommandHandler implements NewCommandSourceHandler {

    private final CurrentAccountWritePlatformService writePlatformService;

    @Autowired
    public CreateCurrentAccountCommandHandler(final CurrentAccountWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.createCurrentAccount(command);
    }
}