package com.algaworks.algashop.ordering.domain.valueobject;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

import static com.algaworks.algashop.ordering.domain.exception.ErrorMessages.VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST;

public record BirthDate(LocalDate value) {

    public BirthDate {
        Objects.requireNonNull(value);
        if (value.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST);
        }
    }

    public Integer age() {
        return Period.between(value, LocalDate.now()).getYears();
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}
