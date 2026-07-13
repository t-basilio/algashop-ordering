package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCartTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.exception.ShoppingCartCantProceedToCheckoutException;
import com.algaworks.algashop.ordering.domain.model.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.Shipping;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CheckoutServiceTest {

    private final CheckoutService checkoutService = new CheckoutService();

    @Test
    void givenValidShoppingCart_whenCheckout_thenReturnPlacedOrderAndEmptyShoppingCart() {

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();

        Money expectedTotalAmount = shoppingCart.totalAmount().add(shipping.cost());
        Quantity expectedTotalItems = shoppingCart.totalItems();

        Order orderPlaced = checkoutService
                .checkout(shoppingCart, billing, shipping, PaymentMethod.CREDIT_CARD);

        Assertions.assertThat(orderPlaced.customerId()).isEqualTo(shoppingCart.customerId());
        Assertions.assertThat(orderPlaced.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        Assertions.assertThat(orderPlaced.billing()).isEqualTo(billing);
        Assertions.assertThat(orderPlaced.shipping()).isEqualTo(shipping);
        Assertions.assertThat(orderPlaced.totalItems()).isEqualTo(expectedTotalItems);
        Assertions.assertThat(orderPlaced.totalAmount()).isEqualTo(expectedTotalAmount);
        Assertions.assertThat(orderPlaced.isPlaced()).isTrue();
        Assertions.assertThat(shoppingCart.items()).isEmpty();
        Assertions.assertThat(shoppingCart.totalItems().value()).isZero();
    }

    @Test
    void givenUnavailableItemsInShoppingCart_whenCheckout_thenCantProceedToCheckout() {

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
                .withUnavailableProduct(true).build();
        Assertions.assertThat(shoppingCart.containsUnavailableItems()).isTrue();

        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();

        Assertions.assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() -> checkoutService
                        .checkout(shoppingCart, billing, shipping, PaymentMethod.CREDIT_CARD));

        Assertions.assertThat(shoppingCart.isEmpty()).isFalse();
    }

    @Test
    void givenEmptyShoppingCart_whenCheckout_thenthenCantProceedToCheckout() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();

        Assertions.assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() -> checkoutService
                        .checkout(shoppingCart, billing, shipping, PaymentMethod.CREDIT_CARD));

        Assertions.assertThat(shoppingCart.isEmpty()).isTrue();
    }

    @Test
    void givenUnavailableItemsInShoppingCart_whenCheckout_thenNotModifyShoppingCartState() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
                .withUnavailableProduct(true).build();
        Assertions.assertThat(shoppingCart.containsUnavailableItems()).isTrue();

        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();

        Assertions.assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() -> checkoutService
                        .checkout(shoppingCart, billing, shipping, PaymentMethod.CREDIT_CARD));

        Assertions.assertThat(shoppingCart.isEmpty()).isFalse();

        Assertions.assertThat(shoppingCart.totalAmount()).hasToString("3400.00");
        Assertions.assertThat(shoppingCart.totalItems()).isEqualTo(new Quantity(3));
        Assertions.assertThat(shoppingCart.items()).hasSize(2);
    }
}