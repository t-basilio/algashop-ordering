package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.exception.OrderCannotBeEditedException;
import com.algaworks.algashop.ordering.domain.model.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.Shipping;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class OrderChangingTest {

    @Test
    void givenDraftedOrder_whenChangeIsPerformed_thenNotThrowException() {
        Order draftedOrder = OrderTestDataBuilder.anOrder().build();

        Product product = ProductTestDataBuilder.aProductAltMousePad().build();
        Quantity quantity = new Quantity(2);
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        PaymentMethod method = PaymentMethod.CREDIT_CARD;

        OrderItem orderItem = draftedOrder.items().iterator().next();

        assertThatCode(() -> draftedOrder.addItem(product, quantity)).doesNotThrowAnyException();
        assertThatCode(() -> draftedOrder.changeBilling(billing)).doesNotThrowAnyException();
        assertThatCode(() -> draftedOrder.changeShipping(shipping)).doesNotThrowAnyException();
        assertThatCode(() -> draftedOrder.changePaymentMethod(method)).doesNotThrowAnyException();
        assertThatCode(() -> draftedOrder.changeItemQuantity(orderItem.id(), quantity)).doesNotThrowAnyException();
    }

    @Test
    void givenPlacedOrder_whenChangeBillingIsCalled_thenThrowOrderCannotBeEditedException() {
        Order placedOrder = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        Billing billing = OrderTestDataBuilder.aBilling();

        assertThatThrownBy(() -> placedOrder.changeBilling(billing))
                .isInstanceOf(OrderCannotBeEditedException.class);
    }

    @Test
    void givenPlacedOrder_whenChangeShippingIsCalled_thenThrowOrderCannotBeEditedException() {
        Order placedOrder = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        Shipping shipping = OrderTestDataBuilder.aShipping();

        assertThatThrownBy(() -> placedOrder.changeShipping(shipping))
                .isInstanceOf(OrderCannotBeEditedException.class);
    }

    @Test
    void givenPlacedOrder_whenChangeItemQuantityIsCalled_thenThrowOrderCannotBeEditedException() {
        Order placedOrder = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        Quantity quantity = new Quantity(5);

        OrderItem orderItem = placedOrder.items().iterator().next();

        ThrowableAssert.ThrowingCallable changeItemQuantityTask = () -> placedOrder
                .changeItemQuantity(orderItem.id(), quantity);

        assertThatThrownBy(changeItemQuantityTask)
                .isInstanceOf(OrderCannotBeEditedException.class);
    }

    @Test
    void givenPlacedOrder_whenChangePaymentMethodIsCalled_thenThrowOrderCannotBeEditedException() {
        Order placedOrder = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        PaymentMethod method = PaymentMethod.GATEWAY_BALANCE;

        assertThatThrownBy(() -> placedOrder.changePaymentMethod(method))
                .isInstanceOf(OrderCannotBeEditedException.class);
    }
}