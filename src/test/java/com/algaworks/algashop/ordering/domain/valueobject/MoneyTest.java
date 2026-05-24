package com.algaworks.algashop.ordering.domain.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class MoneyTest {

    @Test
    void shouldGenerateMoney() {
        Money money = new Money("2.50");
        Assertions.assertThat(money.value()).isEqualTo("2.50");
    }

    @Test
    void shouldNotGenerateWhenItsNegative() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Money("-1"));
    }

    @Test
    void shouldMultiplyWhenCallMethod() {
        Money money = new Money(new BigDecimal("2.50"));
        Quantity added = new Quantity(2).add(new Quantity(2));

        Assertions.assertThat(money.multiply(added)).isEqualTo(new Money("10.00"));
    }

    @Test
    void shouldDivideWhenCallMethod() {
        Money money = new Money(new BigDecimal("10"));
        Money quarter = new Money("4");

        Assertions.assertThat(money.divide(quarter)).isEqualTo(new Money("2.50"));
    }
}
