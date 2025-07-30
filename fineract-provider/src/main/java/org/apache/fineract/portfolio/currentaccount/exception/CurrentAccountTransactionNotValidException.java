// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/exception/CurrentAccountTransactionNotValidException.java

package org.apache.fineract.portfolio.currentaccount.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * Exception thrown when a current account transaction is not valid
 */
public class CurrentAccountTransactionNotValidException extends AbstractPlatformDomainRuleException {

    private static final String ERROR_CODE = "error.msg.current.account.transaction.not.valid";

    public CurrentAccountTransactionNotValidException(final String message) {
        super(ERROR_CODE, message);
    }

    public CurrentAccountTransactionNotValidException(final String message, final Object... defaultUserMessages) {
        super(ERROR_CODE, message, defaultUserMessages);
    }
}