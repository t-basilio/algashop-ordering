package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Product;
import com.algaworks.algashop.ordering.domain.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ShoppingCartItemId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ShoppingCartItemTest {

    @Test
    void givenValidData_whenCreateNewItem_thenInitializeCorrectly() {
        ShoppingCartItem cartItem = ShoppingCartItemTestDataBuilder.aShoppingCartItem().buildWithProduct();

        Assertions.assertWith(cartItem,
                i -> Assertions.assertThat(i.id()).isNotNull(),
                i -> Assertions.assertThat(i.shoppingCartId()).isNotNull(),
                i -> Assertions.assertThat(i.productId()).isNotNull(),
                i -> Assertions.assertThat(i.productName()).hasToString("Notebook X11"),
                i -> Assertions.assertThat(i.price()).hasToString("3000.00"),
                i -> Assertions.assertThat(i.quantity()).hasToString("1"),
                i -> Assertions.assertThat(i.isAvailable()).isTrue(),
                i -> Assertions.assertThat(i.totalAmount()).hasToString("3000.00")
        );
    }

    @Test
    void givenCartItem_whenChangeQuantity_thenRecalculateTotal() {
        ShoppingCartItem cartItem = ShoppingCartItemTestDataBuilder.aShoppingCartItem()
                .price(new Money("1000"))
                .quantity(new Quantity(1))
                .build();

        cartItem.changeQuantity(new Quantity(3));

        Assertions.assertWith(cartItem,
                i -> Assertions.assertThat(i.quantity()).isEqualTo(new Quantity(3)),
                i -> Assertions.assertThat(i.totalAmount()).isEqualTo(new Money("3000"))
        );
    }

    @Test
    void givenCartItem_whenChangePrice_thenRecalculateTotal() {
        ShoppingCartItem cartItem = ShoppingCartItemTestDataBuilder.aShoppingCartItem()
                .productName(new ProductName("Some product"))
                .price(new Money("1500"))
                .quantity(new Quantity(3))
                .build();

        Product product = ProductTestDataBuilder.aProduct().build();
        Money totalAmountCartItem = product.price().multiply(cartItem.quantity());

        cartItem.refresh(product);

        Assertions.assertWith(cartItem,
                i -> Assertions.assertThat(i.price()).isEqualTo(product.price()),
                i -> Assertions.assertThat(i.totalAmount()).isEqualTo(totalAmountCartItem)
        );
    }

    @Test
    void givenCartItem_whenChangeAvailability_thenUpdateStatus() {
        ShoppingCartItem cartItem = ShoppingCartItemTestDataBuilder.aShoppingCartItem()
                .available(true)
                .build();

        Product product = ProductTestDataBuilder.aProduct()
                .inStock(false)
                .build();

        cartItem.refresh(product);

        Assertions.assertThat(cartItem.isAvailable()).isFalse();
    }

    @Test
    void givenEqualIds_whenCompareItems_thenBeEqual() {
        ShoppingCartId cartId = new ShoppingCartId();
        ProductId productId = new ProductId();
        ShoppingCartItemId shoppingCartItemId = new ShoppingCartItemId();

        ShoppingCartItem cartItem1 = ShoppingCartItem.existing()
                .id(shoppingCartItemId)
                .shoppingCartId(cartId)
                .productId(productId)
                .productName(new ProductName("Mouse"))
                .price(new Money("100"))
                .quantity(new Quantity(1))
                .available(true)
                .totalAmount(new Money("100"))
                .build();

        ShoppingCartItem cartItem2 = ShoppingCartItem.existing()
                .id(shoppingCartItemId)
                .shoppingCartId(cartId)
                .productId(productId)
                .productName(new ProductName("Notebook"))
                .price(new Money("100"))
                .quantity(new Quantity(1))
                .available(true)
                .totalAmount(new Money("100"))
                .build();

        Assertions.assertThat(cartItem1).isEqualTo(cartItem2);
        Assertions.assertThat(cartItem1.hashCode()).hasSameHashCodeAs(cartItem2.hashCode());
    }

    @Test
    void givenDifferentIds_whenCompareItems_thenNotBeEqual() {
        ShoppingCartItem cartItem1 = ShoppingCartItemTestDataBuilder.aShoppingCartItem().buildWithProduct();
        ShoppingCartItem cartItem2 = ShoppingCartItemTestDataBuilder.aShoppingCartItem().buildWithProduct();

        Assertions.assertThat(cartItem1).isNotEqualTo(cartItem2);
    }

}
