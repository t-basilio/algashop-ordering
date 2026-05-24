package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderCancelTest {

    @Test
    void givenEmptyOrder_whenCancel_thenChangeStatusToCanceled() {
        Order order = Order.draft(new CustomerId());

        Assertions.assertThat(order.isCanceled()).isFalse();

        order.cancel();

        Assertions.assertWith(order,
                o -> Assertions.assertThat(o.isCanceled()).isTrue(),
                o -> Assertions.assertThat(o.canceledAt()).isNotNull()
        );
    }

    @Test
    void givenFilledOrder_whenCancel_thenChangeStatusToCanceled() {
        Order draftedOrder = OrderTestDataBuilder.anOrder().build();

        Assertions.assertThat(draftedOrder.isCanceled()).isFalse();
        draftedOrder.cancel();

        Assertions.assertWith(draftedOrder,
                o -> Assertions.assertThat(draftedOrder.isCanceled()).isTrue(),
                o -> Assertions.assertThat(draftedOrder.canceledAt()).isNotNull()
        );
    }

    @Test
    void givenPlacedOrder_whenCancel_thenChangeStatusToCanceled() {
        Order placedOrder = OrderTestDataBuilder
                .anOrder().status(OrderStatus.PLACED).build();

        Assertions.assertThat(placedOrder.isCanceled()).isFalse();
        placedOrder.cancel();

        Assertions.assertWith(placedOrder,
                o -> Assertions.assertThat(placedOrder.isCanceled()).isTrue(),
                o -> Assertions.assertThat(placedOrder.canceledAt()).isNotNull()
        );
    }

    @Test
    void givenPaidOrder_whenCancel_thenChangeStatusToCanceled() {
        Order paidOrder = OrderTestDataBuilder
                .anOrder().status(OrderStatus.PAID).build();

        Assertions.assertThat(paidOrder.isCanceled()).isFalse();
        paidOrder.cancel();

        Assertions.assertWith(paidOrder,
                o -> Assertions.assertThat(paidOrder.isCanceled()).isTrue(),
                o -> Assertions.assertThat(paidOrder.canceledAt()).isNotNull()
        );
    }

    @Test
    void givenReadyOrder_whenCancel_thenChangeStatusToCanceled() {
        Order readyOrder = OrderTestDataBuilder
                .anOrder().status(OrderStatus.READY).build();

        Assertions.assertThat(readyOrder.isCanceled()).isFalse();
        readyOrder.cancel();

        Assertions.assertWith(readyOrder,
                o -> Assertions.assertThat(readyOrder.isCanceled()).isTrue(),
                o -> Assertions.assertThat(readyOrder.canceledAt()).isNotNull()
        );
    }

    @Test
    void givenCanceledOrder_whenTryToCancel_thenThrowException() {
        Order canceledOrder = OrderTestDataBuilder
                .anOrder().status(OrderStatus.CANCELED).build();

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                        .isThrownBy(canceledOrder::cancel);

        Assertions.assertWith(canceledOrder,
                o -> Assertions.assertThat(canceledOrder.isCanceled()).isTrue(),
                o -> Assertions.assertThat(canceledOrder.canceledAt()).isNotNull()
        );
    }
}
