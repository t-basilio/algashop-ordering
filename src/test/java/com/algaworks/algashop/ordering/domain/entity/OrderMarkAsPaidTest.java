package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderMarkAsPaidTest {

    @Test
    void givenPlacedOrder_whenMarkAsPaid_thenChangeToPaid() {
        Order placedOrder = OrderTestDataBuilder
                .anOrder().status(OrderStatus.PLACED).build();

        placedOrder.markAsPaid();
        Assertions.assertThat(placedOrder.isPaid()).isTrue();
        Assertions.assertThat(placedOrder.paidAt()).isNotNull();
    }

    @Test
    void givenDraftedOrder_whenTryToMarkAsPaid_thenThrowException() {
        Order draftedOrder = OrderTestDataBuilder.anOrder().build();

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(draftedOrder::markAsPaid);

        Assertions.assertThat(draftedOrder.isPaid()).isFalse();
        Assertions.assertThat(draftedOrder.paidAt()).isNull();
    }

    @Test
    void givenPaidOrder_whenTryToMarkAsPaid_thenThrowException() {
        Order paidOrder = OrderTestDataBuilder
                .anOrder().status(OrderStatus.PAID).build();

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(paidOrder::markAsPaid);

        Assertions.assertThat(paidOrder.isPaid()).isTrue();
        Assertions.assertThat(paidOrder.paidAt()).isNotNull();
    }
}
