// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/exception/CurrentAccountProductDuplicateNameException.java

package org.apache.fineract.portfolio.currentaccount.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * Exception thrown when trying to create a current account product with a duplicate name
 */
public class CurrentAccountProductDuplicateNameException extends AbstractPlatformDomainRuleException {

    public CurrentAccountProductDuplicateNameException(final String name) {
        super("error.msg.currentaccountproduct.duplicate.name",
                "Current account product with name '" + name + "' already exists", name);
    }

    public CurrentAccountProductDuplicateNameException(final String fieldName, final String name) {
        super("error.msg.currentaccountproduct.duplicate." + fieldName,
                "Current account product with " + fieldName + " '" + name + "' already exists", name);
    }
}