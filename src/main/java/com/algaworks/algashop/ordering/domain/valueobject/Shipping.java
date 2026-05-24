package com.algaworks.algashop.ordering.domain.valueobject;

import lombok.Builder;

import java.time.LocalDate;
import java.util.Objects;

@Builder(toBuilder = true)
public record Shipping(
        Recipient recipient,
        Address address,
        Money cost,
        LocalDate expectedDate) {

    public Shipping {
        Objects.requireNonNull(recipient);
        Objects.requireNonNull(address);
        Objects.requireNonNull(cost);
        Objects.requireNonNull(expectedDate);
    }
}
