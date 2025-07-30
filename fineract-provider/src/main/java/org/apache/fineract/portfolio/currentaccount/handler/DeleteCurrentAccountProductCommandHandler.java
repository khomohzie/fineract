// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/handler/DeleteCurrentAccountProductCommandHandler.java

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
 * Command Handler for deleting Current Account Products
 */
@Service
@CommandType(entity = "CURRENTACCOUNTPRODUCT", action = "DELETE")
public class DeleteCurrentAccountProductCommandHandler implements NewCommandSourceHandler {

    private final CurrentAccountProductWritePlatformService writePlatformService;

    @Autowired
    public DeleteCurrentAccountProductCommandHandler(final CurrentAccountProductWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.deleteCurrentAccountProduct(command.entityId());
    }
}