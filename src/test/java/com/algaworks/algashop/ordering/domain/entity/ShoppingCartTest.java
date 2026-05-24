package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.ShoppingCartDoesNotContainItemException;
import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Product;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ShoppingCartItemId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ShoppingCartTest {

    @Test
    void givenCustomer_whenStartShopping_thenInitializeEmptyCart() {
        var customerId = new CustomerId();

        ShoppingCart cart = ShoppingCart.startShopping(customerId);

        Assertions.assertWith(cart,
                c -> Assertions.assertThat(c.id()).isNotNull(),
                c -> Assertions.assertThat(c.customerId()).isEqualTo(customerId),
                c -> Assertions.assertThat(c.totalAmount()).isEqualTo(Money.ZERO),
                c -> Assertions.assertThat(c.totalItems()).isEqualTo(Quantity.ZERO),
                c -> Assertions.assertThat(c.isEmpty()).isTrue(),
                c -> Assertions.assertThat(c.items()).isEmpty()
        );
    }

    @Test
    void givenEmptyCart_whenAddNewItem_thenContainItemAndRecalculateTotals() {
        ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        Product product = ProductTestDataBuilder.aProduct().build();

        cart.addItem(product, new Quantity(3));

        Assertions.assertThat(cart.items()).hasSize(1);
        var item = cart.items().iterator().next();
        Assertions.assertThat(item.productId()).isEqualTo(product.id());
        Assertions.assertThat(item.quantity()).isEqualTo(new Quantity(3));
        Assertions.assertThat(cart.totalItems()).isEqualTo(new Quantity(3));
        Assertions.assertThat(cart.totalAmount()).isEqualTo(product.price().multiply(item.quantity()));
    }

    @Test
    void givenCartWithExistingProduct_whenAddSameProduct_thenIncrementQuantity() {
        ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        Product product = ProductTestDataBuilder.aProduct().build();

        cart.addItem(product, new Quantity(3));
        cart.addItem(product, new Quantity(3));
        var cartItem = cart.items().iterator().next();

        Assertions.assertThat(cart.items()).hasSize(1);
        Assertions.assertThat(cartItem.quantity()).hasToString("6");
    }

    @Test
    void givenCartWithItems_whenRemoveExistingItem_thenRemoveAndRecalculateTotals() {
        ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        var cartItem = cart.items().iterator().next();

        cart.removeItem(cartItem.id());

        Assertions.assertThat(cart.items()).doesNotContain(cartItem);
        Assertions.assertThat(cart.totalItems()).isEqualTo(
                cart.items().stream().map(ShoppingCartItem::quantity).reduce(Quantity.ZERO, Quantity::add)
        );
    }

    @Test
    void givenCartWithItems_whenRemoveNonexistentItem_thenThrowShoppingCartDoesNotContainItemException() {
        ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        ShoppingCartItemId randomId = new ShoppingCartItemId();

        Assertions.assertThatExceptionOfType(ShoppingCartDoesNotContainItemException.class)
                .isThrownBy(() -> cart.removeItem(randomId));
    }

    @Test
    void givenCartWithItems_whenEmpty_thenClearAllItemsAndResetTotals() {
        ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart().build();

        cart.empty();

        Assertions.assertWith(cart,
                c -> Assertions.assertThat(c.isEmpty()).isTrue(),
                c -> Assertions.assertThat(c.totalItems()).isEqualTo(Quantity.ZERO),
                c -> Assertions.assertThat(c.totalAmount()).isEqualTo(Money.ZERO)
        );
    }

    @Test
    void givenCartWithItems_whenChangeItemPrice_thenRecalculateTotalAmount() {
        ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();

        Product product = ProductTestDataBuilder.aProduct()
                .build();

        cart.addItem(product, new Quantity(2));

        product = ProductTestDataBuilder.aProduct()
                .price(new Money("50"))
                .build();
        cart.refreshItem(product);

        var cartItem = cart.findItem(product.id());

        Assertions.assertThat(cartItem.price()).isEqualTo(new Money("50"));
        Assertions.assertThat(cart.totalAmount()).isEqualTo(new Money("100"));
    }

    @Test
    void givenCartWithItems_whenDetectUnavailableItems_thenReturnTrue() {
        ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        Product product = ProductTestDataBuilder.aProduct().inStock(false).build();
        cart.refreshItem(product);

        Assertions.assertThat(cart.containsUnavailableItems()).isTrue();
    }

    @Test
    void givenCartWithItems_whenChangeQuantityToZero_thenThrowIllegalArgumentException() {
        ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        var cartItem = cart.items().iterator().next();

        Assertions.assertThatIllegalArgumentException()
                .isThrownBy(() -> cart.changeItemQuantity(cartItem.id(), Quantity.ZERO));
    }

    @Test
    void givenCartWithItems_whenChangeItemQuantity_thenRecalculateTotalItems() {
        ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        var cartItem = cart.items().iterator().next();

        cart.changeItemQuantity(cartItem.id(), new Quantity(5));

        Assertions.assertThat(cart.totalItems()).isEqualTo(
                new Quantity(cart.items().stream().mapToInt(i -> i.quantity().value()).sum())
        );
    }

    @Test
    void givenCartWithItems_whenFindItemById_thenReturnItem() {
        ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        var cartItem = cart.items().iterator().next();

        var found = cart.findItem(cartItem.id());

        Assertions.assertThat(found).isEqualTo(cartItem);
    }

    @Test
    void givenDifferentIds_whenCompareItems_thenNotBeEqual() {
        ShoppingCart shoppingCart1 = ShoppingCartTestDataBuilder.aShoppingCart().build();
        ShoppingCart shoppingCart2 = ShoppingCartTestDataBuilder.aShoppingCart().build();

        Assertions.assertThat(shoppingCart1).isNotEqualTo(shoppingCart2);
    }
}

