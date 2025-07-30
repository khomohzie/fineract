// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/handler/UpdateCurrentAccountProductCommandHandler.java

package org.apache.fineract.portfolio.currentaccount.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.currentaccount.service.CurrentAccountProductWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Command Handler for updating Current Account Products
 */
@Service
@CommandType(entity = "CURRENTACCOUNTPRODUCT", action = "UPDATE")
public class UpdateCurrentAccountProductCommandHandler implements NewCommandSourceHandler {

    private final CurrentAccountProductWritePlatformService writePlatformService;

    @Autowired
    public UpdateCurrentAccountProductCommandHandler(final CurrentAccountProductWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.updateCurrentAccountProduct(command.entityId(), command);
    }
}
