package com.algaworks.algashop.ordering.domain.exception;

import static com.algaworks.algashop.ordering.domain.exception.ErrorMessages.ERROR_CUSTOMER_ARCHIVED;

public class CustomerArchivedException extends DomainException {

    public CustomerArchivedException(Throwable cause) {
        super(ERROR_CUSTOMER_ARCHIVED, cause);
    }

    public CustomerArchivedException() {
        super(ERROR_CUSTOMER_ARCHIVED);
    }
}
