// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/exception/CurrentAccountDuplicateAccountNumberException.java

package org.apache.fineract.portfolio.currentaccount.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * Exception thrown when trying to create a current account with a duplicate account number
 */
public class CurrentAccountDuplicateAccountNumberException extends AbstractPlatformDomainRuleException {

    public CurrentAccountDuplicateAccountNumberException(final String accountNumber) {
        super("error.msg.currentaccount.duplicate.accountNumber",
                "Current account with account number '" + accountNumber + "' already exists", accountNumber);
    }
}