package com.algaworks.algashop.ordering.domain.model.valueobject;

import java.util.Objects;

public record FullName(String firstName, String lastName) {

    public FullName(String firstName, String lastName) {
        Objects.requireNonNull(firstName, lastName);

        if (firstName.isBlank() || lastName.isBlank()) {
            throw new IllegalArgumentException();
        }

        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
