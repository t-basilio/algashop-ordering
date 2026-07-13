package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;

public class ShoppingCartTestDataBuilder {

    public CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
    public static final ShoppingCartId DEFAULT_SHOPPING_CART_ID = new ShoppingCartId();
    private boolean withItems = true;
    private boolean withUnavailableProduct = false;

    private ShoppingCartTestDataBuilder() {}

    public static ShoppingCartTestDataBuilder aShoppingCart() {
        return new ShoppingCartTestDataBuilder();
    }

    public ShoppingCartTestDataBuilder customerId(CustomerId customerId) {
        this.customerId = customerId;
        return this;
    }

    public ShoppingCartTestDataBuilder withItems(boolean withItems) {
        this.withItems = withItems;
        return this;
    }

    public ShoppingCartTestDataBuilder withUnavailableProduct(boolean withUnavailableProduct) {
        this.withUnavailableProduct = withUnavailableProduct;
        return this;
    }

    public ShoppingCart build() {
        ShoppingCart cart = ShoppingCart.startShopping(customerId);

        if(withItems) {
            cart.addItem(
                    ProductTestDataBuilder.aProductAltRamMemory().build(),
                    new Quantity(2));

            cart.addItem(
                    ProductTestDataBuilder.aProduct().build(),
                    new Quantity(1));
        }

        if (withUnavailableProduct) {

            Product productModified = ProductTestDataBuilder.aProduct()
                    .inStock(false).build();

            cart.refreshItem(productModified);
        }

        return cart;
    }

}
