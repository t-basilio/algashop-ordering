package com.algaworks.algashop.ordering.domain.model.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal value) implements Comparable<Money>{

    private static final RoundingMode roundingMode = RoundingMode.HALF_EVEN;
    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money(String value) {
        this(new BigDecimal(value));
    }

    public Money(BigDecimal value) {
        Objects.requireNonNull(value);
        this.value = value.setScale(2, roundingMode);
        if (this.value.signum() == -1) {
            throw new IllegalArgumentException();
        }
    }

    public Money multiply(Quantity quantity) {
        Objects.requireNonNull(quantity);
        if (quantity.value() < 1) {
            throw new IllegalArgumentException();
        }
        BigDecimal multiplied = this.value.multiply(new BigDecimal(quantity.value()));
        return new Money(multiplied);
    }

    public Money add(Money other) {
        Objects.requireNonNull(other);
        return new Money(this.value.add(other.value()));
    }

    public Money divide(Money other) {
        Objects.requireNonNull(other);
        return new Money(this.value.divide(other.value(), roundingMode));
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    @Override
    public int compareTo(Money other) {
        return this.value.compareTo(other.value);
    }
}
