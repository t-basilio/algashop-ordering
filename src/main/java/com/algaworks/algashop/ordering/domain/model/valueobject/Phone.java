package com.algaworks.algashop.ordering.domain.model.valueobject;

import java.util.Objects;

import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessages.VALIDATION_ERROR_PHONE_IS_BLANK;

public record Phone(String value) {

    public Phone {
        Objects.requireNonNull(value);
        if (value.isBlank()) {
            throw new IllegalArgumentException(VALIDATION_ERROR_PHONE_IS_BLANK);
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
