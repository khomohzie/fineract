// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/exception/CurrentAccountTransactionNotFoundException.java

package org.apache.fineract.portfolio.currentaccount.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * Exception thrown when a current account transaction is not found
 */
public class CurrentAccountTransactionNotFoundException extends AbstractPlatformResourceNotFoundException {

    public CurrentAccountTransactionNotFoundException(final Long id) {
        super("error.msg.currentaccount.transaction.id.invalid",
                "Current account transaction with identifier " + id + " does not exist", id);
    }
}
