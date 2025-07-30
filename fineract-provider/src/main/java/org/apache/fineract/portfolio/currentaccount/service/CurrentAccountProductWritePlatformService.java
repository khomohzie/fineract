// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/service/CurrentAccountProductWritePlatformService.java

package org.apache.fineract.portfolio.currentaccount.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

/**
 * Interface for writing Current Account Product data
 * This service handles all write operations (create, update, delete) for current account products
 */
public interface CurrentAccountProductWritePlatformService {

    /**
     * Create a new current account product
     */
    CommandProcessingResult createCurrentAccountProduct(JsonCommand command);

    /**
     * Update an existing current account product
     */
    CommandProcessingResult updateCurrentAccountProduct(Long productId, JsonCommand command);

    /**
     * Delete a current account product
     */
    CommandProcessingResult deleteCurrentAccountProduct(Long productId);
}