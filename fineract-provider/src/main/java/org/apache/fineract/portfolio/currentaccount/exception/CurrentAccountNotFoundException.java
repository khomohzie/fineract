// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/exception/CurrentAccountNotFoundException.java

package org.apache.fineract.portfolio.currentaccount.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * Exception thrown when a current account is not found
 */
public class CurrentAccountNotFoundException extends AbstractPlatformResourceNotFoundException {

    public CurrentAccountNotFoundException(final Long id) {
        super("error.msg.currentaccount.id.invalid", "Current account with identifier " + id + " does not exist", id);
    }

    public CurrentAccountNotFoundException(final String accountNumber) {
        super("error.msg.currentaccount.accountNumber.invalid", "Current account with account number " + accountNumber + " does not exist", accountNumber);
    }
}