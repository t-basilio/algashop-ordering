package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Product;
import com.algaworks.algashop.ordering.domain.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ShoppingCartId;

public class ShoppingCartItemTestDataBuilder {

    private ShoppingCartId shoppingCartId = ShoppingCartTestDataBuilder.DEFAULT_SHOPPING_CART_ID;
    private ProductId productId = ProductTestDataBuilder.DEFAULT_PRODUCT_ID;
    private ProductName productName = new ProductName("Notebook");
    private Money price = new Money("1000");
    private Quantity quantity = new Quantity(1);
    private boolean available = true;

    private ShoppingCartItemTestDataBuilder() {}

    public static ShoppingCartItemTestDataBuilder aShoppingCartItem() {
        return new ShoppingCartItemTestDataBuilder();
    }

    public ShoppingCartItemTestDataBuilder shoppingCartId(ShoppingCartId shoppingCartId) {
        this.shoppingCartId = shoppingCartId;
        return this;
    }

    public ShoppingCartItemTestDataBuilder productId(ProductId productId) {
        this.productId = productId;
        return this;
    }

    public ShoppingCartItemTestDataBuilder productName(ProductName productName) {
        this.productName = productName;
        return this;
    }

    public ShoppingCartItemTestDataBuilder price(Money price) {
        this.price = price;
        return this;
    }

    public ShoppingCartItemTestDataBuilder quantity(Quantity quantity) {
        this.quantity = quantity;
        return this;
    }

    public ShoppingCartItemTestDataBuilder available(boolean available) {
        this.available = available;
        return this;
    }

    public ShoppingCartItem build() {
        Product product = new Product(this.productId, this.productName, this.price, true);
        return ShoppingCartItem.brandNew()
                .shoppingCartId(shoppingCartId)
                .product(product)
                .quantity(quantity)
                .build();
    }

    public ShoppingCartItem buildWithProduct() {
        Product product = ProductTestDataBuilder.aProduct().build();
        return ShoppingCartItem.brandNew()
                .shoppingCartId(shoppingCartId)
                .product(product)
                .quantity(quantity)
                .build();
    }
}
