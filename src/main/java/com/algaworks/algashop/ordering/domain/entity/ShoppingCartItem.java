package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.ShoppingCartItemIncompatibleProductException;
import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Product;
import com.algaworks.algashop.ordering.domain.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ShoppingCartItemId;
import lombok.Builder;

import java.util.Objects;

public class ShoppingCartItem {

    private ShoppingCartItemId id;
    private ShoppingCartId shoppingCartId;

    private ProductId productId;
    private ProductName productName;
    private Money price;

    private Quantity quantity;
    private Money totalAmount;
    private Boolean available;


    @Builder(builderClassName = "ExistingShoppingCartItemBuilder", builderMethodName = "existing")
    public ShoppingCartItem (ShoppingCartItemId id, ShoppingCartId shoppingCartId,
                             ProductId productId, ProductName productName, Money price,
                             Quantity quantity, Money totalAmount, Boolean available) {
        this.setId(id);
        this.setShoppingCartId(shoppingCartId);
        this.setProductId(productId);
        this.setProductName(productName);
        this.setPrice(price);
        this.setQuantity(quantity);
        this.setTotalAmount(totalAmount);
        this.setAvailable(available);
    }

    @Builder(builderClassName = "BrandNewShoppingCartItemBuilder", builderMethodName = "brandNew")
    public static ShoppingCartItem createBrandNew(ShoppingCartId shoppingCartId,
                                                  Product product,
                                                  Quantity quantity) {
        Objects.requireNonNull(shoppingCartId);
        Objects.requireNonNull(product);
        Objects.requireNonNull(quantity);

        ShoppingCartItem shoppingCartItem = new ShoppingCartItem(
                new ShoppingCartItemId(),
                shoppingCartId,
                product.id(),
                product.name(),
                product.price(),
                quantity,
                Money.ZERO,
                product.inStock()
        );

        shoppingCartItem.recalculateTotals();
        return shoppingCartItem;
    }

    void refresh(Product product) {
        Objects.requireNonNull(product);
        Objects.requireNonNull(product.id());

        if (!product.id().equals(this.productId())) {
            throw new ShoppingCartItemIncompatibleProductException(this.id(), this.productId());
        }

        this.setProductName(product.name());
        this.setPrice(product.price());
        this.setAvailable(product.inStock());
        this.recalculateTotals();
    }

    void changeQuantity(Quantity quantity) {
        Objects.requireNonNull(quantity);
        this.setQuantity(quantity);
        this.recalculateTotals();
    }

    public ShoppingCartItemId id() {
        return id;
    }

    public ShoppingCartId shoppingCartId() {
        return shoppingCartId;
    }

    public ProductId productId() {
        return productId;
    }

    public ProductName productName() {
        return productName;
    }

    public Money price() {
        return price;
    }

    public Quantity quantity() {
        return quantity;
    }

    public Money totalAmount() {
        return totalAmount;
    }

    public Boolean isAvailable() {
        return available;
    }

    public boolean isNotAvailable() {
        return !isAvailable();
    }

    private void recalculateTotals() {
        this.setTotalAmount(this.price().multiply(this.quantity()));
    }

    private void setId(ShoppingCartItemId id) {
        Objects.requireNonNull(id);
        this.id = id;
    }

    private void setShoppingCartId(ShoppingCartId shoppingCartId) {
        Objects.requireNonNull(shoppingCartId);
        this.shoppingCartId = shoppingCartId;
    }

    private void setProductId(ProductId productId) {
        Objects.requireNonNull(productId);
        this.productId = productId;
    }

    private void setProductName(ProductName productName) {
        Objects.requireNonNull(productName);
        this.productName = productName;
    }

    private void setPrice(Money price) {
        Objects.requireNonNull(price);
        this.price = price;
    }

    private void setQuantity(Quantity quantity) {
        Objects.requireNonNull(quantity);
        if (quantity.equals(Quantity.ZERO))
            throw new IllegalArgumentException("Must be greater tah zero");

        this.quantity = quantity;
    }

    private void setTotalAmount(Money totalAmount) {
        Objects.requireNonNull(totalAmount);
        this.totalAmount = totalAmount;
    }

    private void setAvailable(Boolean available) {
        Objects.requireNonNull(available);
        this.available = available;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingCartItem shoppingCartItem = (ShoppingCartItem) o;
        return Objects.equals(id, shoppingCartItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
