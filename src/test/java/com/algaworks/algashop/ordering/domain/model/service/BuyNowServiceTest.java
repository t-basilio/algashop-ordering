package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderItem;
import com.algaworks.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.entity.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.exception.ProductOutOfStockException;
import com.algaworks.algashop.ordering.domain.model.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.Shipping;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

class BuyNowServiceTest {

    private final BuyNowService buyNowService = new BuyNowService();

    @Test
    void givenValidProduct_whenBuyNow_thenReturnPlacedOrder() {
        Product product = ProductTestDataBuilder.aProduct().build();
        Quantity quantity = new Quantity(2);
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        Money expectedTotalAmount = product.price().multiply(quantity).add(shipping.cost());


        Order order = buyNowService.buyNow(product, customer.id(), billing, shipping, quantity, paymentMethod);
        OrderItem orderItem = order.items().iterator().next();

        Assertions.assertThat(order.customerId()).isEqualTo(customer.id());
        Assertions.assertThat(order.paymentMethod()).isEqualTo(paymentMethod);
        Assertions.assertThat(order.billing()).isEqualTo(billing);
        Assertions.assertThat(order.shipping()).isEqualTo(shipping);
        Assertions.assertThat(orderItem.productId()).isEqualTo(product.id());
        Assertions.assertThat(orderItem.quantity()).isEqualTo(quantity);
        Assertions.assertThat(order.totalItems()).isEqualTo(quantity);
        Assertions.assertThat(order.totalAmount()).isEqualTo(expectedTotalAmount);
        Assertions.assertThat(order.isPlaced()).isTrue();
    }

    @Test
    void givenOutOfStockProduct_whenBuyNow_thenRetunProductOutOfStockException () {
        Product productOutOfStock = ProductTestDataBuilder.aProductUnavaliable().build();
        Quantity quantity = new Quantity(1);
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();

        ThrowingCallable buyNow = () -> buyNowService
                .buyNow(productOutOfStock, customer.id(), billing, shipping, quantity, PaymentMethod.CREDIT_CARD);

        Assertions.assertThatThrownBy(buyNow)
                .isInstanceOf(ProductOutOfStockException.class);
    }

    @Test
    void givenInvalidQuantity_whenBuyNow_thenRetunIllegalArgumentException () {
        Product product = ProductTestDataBuilder.aProduct().build();
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();

        ThrowingCallable buyNow = () -> buyNowService
                .buyNow(product, customer.id(), billing, shipping, Quantity.ZERO, PaymentMethod.CREDIT_CARD);

        Assertions.assertThatThrownBy(buyNow)
                .isInstanceOf(IllegalArgumentException.class);
    }
}