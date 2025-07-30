// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/service/CurrentAccountReadPlatformServiceImpl.java

package org.apache.fineract.portfolio.currentaccount.service;

import org.apache.fineract.portfolio.currentaccount.data.CurrentAccountData;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CurrentAccountReadPlatformServiceImpl implements CurrentAccountReadPlatformService {

    @Override
    public Collection<CurrentAccountData> retrieveAll() {
        // Dummy implementation for now
        return Collections.emptyList();
    }

    @Override
    public Collection<CurrentAccountData> retrieveAllForClient(Long clientId) {
        // Dummy implementation for now
        return Collections.emptyList();
    }

    @Override
    public CurrentAccountData retrieveOne(Long accountId) {
        // Dummy implementation for now
        return null;
    }

    @Override
    public CurrentAccountData retrieveTemplate(Long clientId, Long groupId) {
        // Dummy implementation for now
        return null;
    }
}
