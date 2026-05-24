package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.OrderInvalidShippingDeliveryDateException;
import com.algaworks.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import com.algaworks.algashop.ordering.domain.exception.ProductOutOfStockException;
import com.algaworks.algashop.ordering.domain.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Product;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.Shipping;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

class OrderTest {

    @Test
    void shouldGenerateDraftedOrder() {
        CustomerId customerId = new CustomerId();
        Order order = Order.draft(customerId);

        Assertions.assertWith(order,
            o -> Assertions.assertThat(o.id()).isNotNull(),
            o -> Assertions.assertThat(o.customerId()).isEqualTo(customerId),
            o -> Assertions.assertThat(o.totalAmount()).isEqualTo(Money.ZERO),
            o -> Assertions.assertThat(o.totalItems()).isEqualTo(Quantity.ZERO),
            o -> Assertions.assertThat(o.isDraft()).isTrue(),
            o -> Assertions.assertThat(o.items()).isEmpty(),

                o -> Assertions.assertThat(o.placedAt()).isNull(),
                o -> Assertions.assertThat(o.paidAt()).isNull(),
                o -> Assertions.assertThat(o.canceledAt()).isNull(),
                o -> Assertions.assertThat(o.readyAt()).isNull(),
                o -> Assertions.assertThat(o.billing()).isNull(),
                o -> Assertions.assertThat(o.shipping()).isNull(),
                o -> Assertions.assertThat(o.paymentMethod()).isNull()
        );
    }

    @Test
    void shouldAddItem() {
        Order order = Order.draft(new CustomerId());
        Product mouse = ProductTestDataBuilder.aProductAltMousePad().build();

        order.addItem(
                mouse,
                new Quantity(1)
        );

        Assertions.assertThat(order.items()).hasSize(1);

        OrderItem orderItem = order.items().iterator().next();

        Assertions.assertWith(orderItem,
                i -> Assertions.assertThat(i.id()).isNotNull(),
                i -> Assertions.assertThat(i.productName()).hasToString("Mouse pad"),
                i -> Assertions.assertThat(i.productId()).isEqualTo(mouse.id()),
                i -> Assertions.assertThat(i.price()).hasToString("100.00"),
                i -> Assertions.assertThat(i.quantity()).hasToString("1")
                );
    }

    @Test
    void shouldGenerateExceptionWhenTryChangeItemSet() {
        Order order = Order.draft(new CustomerId());
        Product mouse = ProductTestDataBuilder.aProductAltMousePad().build();

        order.addItem(
                mouse,
                new Quantity(1)
        );

        Set<OrderItem> items = order.items();

        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(items::clear);
    }

    @Test
    void shouldRecalculateTotals() {
        Order order = Order.draft(new CustomerId());

        order.addItem(
               ProductTestDataBuilder.aProductAltMousePad().build(),
                new Quantity(2)
        );

        order.addItem(
                ProductTestDataBuilder.aProductAltRamMemory().build(),
                new Quantity(1)
        );

       Assertions.assertThat(order.totalAmount()).hasToString("400.00");
       Assertions.assertThat(order.totalItems()).hasToString("3");
    }

    @Test
    void givenDraftedOrder_whenPlace_thenChangeToPlaced() {
        Order order = OrderTestDataBuilder.anOrder().build();
        order.place();
        Assertions.assertThat(order.isPlaced()).isTrue();
    }

    @Test
    void givenPlacedOrder_whenTryToPlace_thenThrowException() {
        Order placedOrder = OrderTestDataBuilder
                .anOrder().status(OrderStatus.PLACED).build();

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(placedOrder::place);
    }

    @Test
    void givenDraftedOrder_whenChangePaymentMethod_thenAllowChange() {
        Order order = Order.draft(new CustomerId());
        order.changePaymentMethod(PaymentMethod.CREDIT_CARD);
        Assertions.assertWith(order.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
    }

    @Test
    void givenDraftedOrder_whenChangeBilling_thenAllowChange() {
        Billing billing = OrderTestDataBuilder.aBilling();
        Order order = Order.draft(new CustomerId());
        order.changeBilling(billing);

        Assertions.assertThat(order.billing()).isEqualTo(billing);
    }

    @Test
    void givenDraftedOrder_whenChangeShipping_thenAllowChange() {
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Order order = Order.draft(new CustomerId());

        order.changeShipping(shipping);

        Assertions.assertWith(order,
                o -> Assertions.assertThat(o.shipping()).isEqualTo(shipping));
    }

    @Test
    void givenDraftedOrderAndDeliveryDateInThePast_whenChangeShipping_thenNotAllowChange() {
        LocalDate expectedDeliveryDate = LocalDate.now().minusDays(2);
        Shipping shipping = OrderTestDataBuilder.aShipping().toBuilder()
                .expectedDate(expectedDeliveryDate)
                .build();

        Order order = Order.draft(new CustomerId());

        Assertions.assertThatExceptionOfType(OrderInvalidShippingDeliveryDateException.class)
                        .isThrownBy(() -> order.changeShipping(shipping));
    }

    @Test
    void givenDraftedOrder_whenChangeItem_thenRecalculate() {
        Order order = Order.draft(new CustomerId());

        order.addItem(
                ProductTestDataBuilder.aProductAltMousePad().build(),
                new Quantity(3)
        );

        OrderItem orderItem = order.items().iterator().next();
        order.changeItemQuantity(orderItem.id(), new Quantity(5));

        Assertions.assertWith(order,
                o -> Assertions.assertThat(o.totalAmount()).isEqualTo(new Money("500.00")),
                o -> Assertions.assertThat(o.totalItems()).hasToString("5")
        );
    }

    @Test
    void givenOutOfStockProduct_whenTryToAddToAnOrder_thenNotAllow() {
        Order order = Order.draft(new CustomerId());

        ThrowableAssert.ThrowingCallable addItemTask = () -> order.addItem(
                ProductTestDataBuilder.aProductUnavaliable().build(), new Quantity(1));

        Assertions.assertThatExceptionOfType(ProductOutOfStockException.class).isThrownBy(addItemTask);
    }
}