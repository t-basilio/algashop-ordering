package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.exception.OrderCannotBeEditedException;
import com.algaworks.algashop.ordering.domain.model.exception.OrderDoesNotContainOrderItemException;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderItemId;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

class OrderRemoveItemTest {

    @Test
    void givenDraftedOrder_whenRemoveItem_thenRecalculate() {
        Order draftedOrder = OrderTestDataBuilder.anOrder().build();
        Money totalAmount = draftedOrder.totalAmount();
        Quantity totalItems = draftedOrder.totalItems();

        OrderItem orderItem = draftedOrder.items().iterator().next();

        draftedOrder.removeItem(orderItem.id());

        Assertions.assertWith(draftedOrder,
                o -> Assertions.assertThat(o.totalAmount()).isLessThan(totalAmount),
                o -> Assertions.assertThat(o.totalItems()).isLessThan(totalItems)
        );
    }

    @Test
    void givenDraftedOrder_whenTryToRemoveNoExistingItem_thenThrowOrderDoesNotContainOrderItemException() {
        Order draftedOrder = OrderTestDataBuilder.anOrder().build();
        Money totalAmount = draftedOrder.totalAmount();
        Quantity totalItems = draftedOrder.totalItems();

        ThrowableAssert.ThrowingCallable removeItemTask = () -> draftedOrder.removeItem(new OrderItemId());

        Assertions.assertThatExceptionOfType(OrderDoesNotContainOrderItemException.class)
                .isThrownBy(removeItemTask);

        Assertions.assertWith(draftedOrder,
                o -> Assertions.assertThat(o.totalAmount()).isEqualTo(totalAmount),
                o -> Assertions.assertThat(o.totalItems()).isEqualTo(totalItems)
        );
    }

    @Test
    void givenPlacedOrder_whenTryToRemoveItem_thenThrowOrderCannotBeEditedException() {
        Order placedOrder = OrderTestDataBuilder.anOrder()
                .status(OrderStatus.PLACED).build();
        Money totalAmount = placedOrder.totalAmount();
        Quantity totalItems = placedOrder.totalItems();

        OrderItem orderItem = placedOrder.items().iterator().next();

        ThrowableAssert.ThrowingCallable removeItemTask = () -> placedOrder.removeItem(orderItem.id());

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(removeItemTask);

        Assertions.assertWith(placedOrder,
                o -> Assertions.assertThat(o.totalAmount()).isEqualTo(totalAmount),
                o -> Assertions.assertThat(o.totalItems()).isEqualTo(totalItems)
        );
    }

    @Test
    void givenReadyOrder_whenTryToRemoveItem_thenThrowOrderCannotBeEditedException() {
        Order readyOrder = OrderTestDataBuilder.anOrder()
                .status(OrderStatus.READY).build();
        Money totalAmount = readyOrder.totalAmount();
        Quantity totalItems = readyOrder.totalItems();

        OrderItem orderItem = readyOrder.items().iterator().next();

        ThrowableAssert.ThrowingCallable removeItemTask = () -> readyOrder.removeItem(orderItem.id());

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(removeItemTask);

        Assertions.assertWith(readyOrder,
                o -> Assertions.assertThat(o.totalAmount()).isEqualTo(totalAmount),
                o -> Assertions.assertThat(o.totalItems()).isEqualTo(totalItems)
        );
    }
}
