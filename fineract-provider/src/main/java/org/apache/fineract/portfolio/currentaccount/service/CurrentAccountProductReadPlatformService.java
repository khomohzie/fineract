// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/service/CurrentAccountProductReadPlatformService.java

package org.apache.fineract.portfolio.currentaccount.service;

import org.apache.fineract.portfolio.currentaccount.data.CurrentAccountProductData;

import java.util.Collection;

/**
 * Interface for reading Current Account Product data
 * This service handles all read operations for current account products
 */
public interface CurrentAccountProductReadPlatformService {

    /**
     * Retrieve all current account products
     */
    Collection<CurrentAccountProductData> retrieveAll();

    /**
     * Retrieve a specific current account product by ID
     */
    CurrentAccountProductData retrieveOne(Long productId);

    /**
     * Retrieve template data for creating a new current account product
     * This includes dropdown options like available currencies
     */
    CurrentAccountProductData retrieveTemplate();
}