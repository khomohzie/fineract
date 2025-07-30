// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/service/CurrentAccountReadPlatformService.java

package org.apache.fineract.portfolio.currentaccount.service;

import org.apache.fineract.portfolio.currentaccount.data.CurrentAccountData;

import java.util.Collection;

/**
 * Interface for reading Current Account data
 */
public interface CurrentAccountReadPlatformService {

    Collection<CurrentAccountData> retrieveAll();

    Collection<CurrentAccountData> retrieveAllForClient(Long clientId);

    CurrentAccountData retrieveOne(Long accountId);

    CurrentAccountData retrieveTemplate(Long clientId, Long groupId);
}