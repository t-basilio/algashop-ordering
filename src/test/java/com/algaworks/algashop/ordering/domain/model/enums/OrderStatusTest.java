package com.algaworks.algashop.ordering.domain.model.enums;

import com.algaworks.algashop.ordering.domain.model.entity.OrderStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderStatusTest {

    @Test
    void canChangeTo() {

        Assertions.assertThat(OrderStatus.DRAFT.canChangeTo(OrderStatus.PLACED)).isTrue();
        Assertions.assertThat(OrderStatus.DRAFT.canChangeTo(OrderStatus.CANCELED)).isTrue();

        Assertions.assertThat(OrderStatus.PAID.canChangeTo(OrderStatus.DRAFT)).isFalse();
    }

    @Test
    void canNotChangeTo() {
        Assertions.assertThat(OrderStatus.PLACED.canNotChangeTo(OrderStatus.DRAFT)).isTrue();
    }
}