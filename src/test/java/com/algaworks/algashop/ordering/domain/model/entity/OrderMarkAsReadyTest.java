package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.exception.OrderStatusCannotBeChangedException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderMarkAsReadyTest {

    @Test
    void givenPaidOrder_whenMarkAsReady_thenChangeToReady() {
        Order paidOrder = OrderTestDataBuilder
                .anOrder().status(OrderStatus.PAID).build();

        paidOrder.markAsReady();

        Assertions.assertWith(paidOrder,
                o -> Assertions.assertThat(paidOrder.isReady()).isTrue(),
                o -> Assertions.assertThat(paidOrder.readyAt()).isNotNull()
        );
    }

    @Test
    void givenDraftedOrder_whenTryToMarkAsReady_thenThrowException() {
        Order draftedOrder = OrderTestDataBuilder.anOrder().build();

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(draftedOrder::markAsReady);

        Assertions.assertWith(draftedOrder,
                o -> Assertions.assertThat(draftedOrder.isReady()).isFalse(),
                o -> Assertions.assertThat(draftedOrder.readyAt()).isNull()
        );
    }

    @Test
    void givenPlacedOrder_whenTryToMarkAsReady_thenThrowException() {
        Order placedOrder = OrderTestDataBuilder
                .anOrder().status(OrderStatus.PLACED).build();

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(placedOrder::markAsReady);

        Assertions.assertWith(placedOrder,
                o -> Assertions.assertThat(placedOrder.isReady()).isFalse(),
                o -> Assertions.assertThat(placedOrder.readyAt()).isNull()
        );
    }

    @Test
    void givenReadyOrder_whenTryToMarkAsReady_thenThrowException() {
        Order readyOrder = OrderTestDataBuilder
                .anOrder().status(OrderStatus.READY).build();

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(readyOrder::markAsReady);

        Assertions.assertWith(readyOrder,
                o -> Assertions.assertThat(readyOrder.isReady()).isTrue(),
                o -> Assertions.assertThat(readyOrder.readyAt()).isNotNull()
        );
    }
}
