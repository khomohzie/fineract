// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/exception/CurrentAccountInsufficientFundsException.java

package org.apache.fineract.portfolio.currentaccount.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

import java.math.BigDecimal;

/**
 * Exception thrown when there are insufficient funds for a transaction
 */
public class CurrentAccountInsufficientFundsException extends AbstractPlatformDomainRuleException {

    public CurrentAccountInsufficientFundsException(final BigDecimal requestedAmount, final BigDecimal availableBalance) {
        super("error.msg.currentaccount.insufficient.funds",
                "Insufficient funds. Requested: " + requestedAmount + ", Available: " + availableBalance,
                requestedAmount, availableBalance);
    }
}
