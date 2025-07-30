// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/exception/CurrentAccountProductNotFoundException.java

package org.apache.fineract.portfolio.currentaccount.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * Exception thrown when a current account product is not found
 */
public class CurrentAccountProductNotFoundException extends AbstractPlatformResourceNotFoundException {

    public CurrentAccountProductNotFoundException(final Long id) {
        super("error.msg.currentaccountproduct.id.invalid", "Current account product with identifier " + id + " does not exist", id);
    }

    public CurrentAccountProductNotFoundException(final String name) {
        super("error.msg.currentaccountproduct.name.invalid", "Current account product with name " + name + " does not exist", name);
    }
}
