package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.ShoppingCartDoesNotContainItemException;
import com.algaworks.algashop.ordering.domain.exception.ShoppingCartDoesNotContainProductException;
import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Product;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ShoppingCartItemId;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ShoppingCart {

    private ShoppingCartId id;
    private CustomerId customerId;

    private Money totalAmount;
    private Quantity totalItems;
    private OffsetDateTime createdAt;

    private Set<ShoppingCartItem> items;

    @Builder(builderClassName = "ExistingShoppingCartBuilder", builderMethodName = "existing")
    public ShoppingCart(ShoppingCartId id, CustomerId customerId,
                        Money totalAmount, Quantity totalItems,
                        OffsetDateTime createdAt, Set<ShoppingCartItem> items) {
        this.setId(id);
        this.setCustomerId(customerId);
        this.setTotalAmount(totalAmount);
        this.setTotalItems(totalItems);
        this.setCreatedAt(createdAt);
        this.setItems(items);
    }

    public static ShoppingCart startShopping(CustomerId customerId) {
        return new ShoppingCart(
                new ShoppingCartId(),
                customerId,
                Money.ZERO,
                Quantity.ZERO,
                OffsetDateTime.now(),
                new HashSet<>()
        );
    }

    public void addItem(Product product, Quantity quantity) {
        Objects.requireNonNull(product);
        Objects.requireNonNull(quantity);
        product.checkOutOfStock();

        ShoppingCartItem newItem = ShoppingCartItem.brandNew()
                .shoppingCartId(this.id())
                .product(product)
                .quantity(quantity)
                .build();

        updateOrInsert(product, quantity, newItem);
        this.recalculateTotals();
    }

    public void removeItem(ShoppingCartItemId shoppingCartItemId) {
        ShoppingCartItem foundItem = findItem(shoppingCartItemId);
        this.items.remove(foundItem);
        this.recalculateTotals();
    }

    public ShoppingCartItem findItem(ShoppingCartItemId shoppingCartItemId) {
        Objects.requireNonNull(shoppingCartItemId);
        return this.items.stream()
                .filter(i -> i.id().equals(shoppingCartItemId))
                .findFirst()
                .orElseThrow(() -> new ShoppingCartDoesNotContainItemException(this.id(), shoppingCartItemId));
    }

    public ShoppingCartItem findItem(ProductId productId) {
        Objects.requireNonNull(productId);
        return this.items.stream()
                .filter(i -> i.productId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ShoppingCartDoesNotContainProductException(this.id(), productId));
    }

    public void refreshItem(Product product) {
        ShoppingCartItem foundItem = findItem(product.id());
        foundItem.refresh(product);
        this.recalculateTotals();
    }

    public void changeItemQuantity(ShoppingCartItemId shoppingCartItemId, Quantity quantity) {
        ShoppingCartItem foundItem = findItem(shoppingCartItemId);
        foundItem.changeQuantity(quantity);
        this.recalculateTotals();
    }
    public void empty() {
        this.items.clear();
        this.recalculateTotals();
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    public boolean containsUnavailableItems() {
        return this.items.stream().anyMatch(ShoppingCartItem::isNotAvailable);
    }

    public ShoppingCartId id() {
        return id;
    }

    public CustomerId customerId() {
        return customerId;
    }

    public Money totalAmount() {
        return totalAmount;
    }

    public Quantity totalItems() {
        return totalItems;
    }

    public OffsetDateTime createdAt() {
        return createdAt;
    }

    public Set<ShoppingCartItem> items() {
        return Collections.unmodifiableSet(this.items);
    }

    private void recalculateTotals() {
        Money totalItemsAmount = this.items.stream().map(ShoppingCartItem::totalAmount)
                .reduce(Money.ZERO, Money::add);

        Quantity totalItemsQuantity = this.items.stream().map(ShoppingCartItem::quantity)
                .reduce(Quantity.ZERO, Quantity::add);

        this.setTotalAmount(totalItemsAmount);
        this.setTotalItems(totalItemsQuantity);
    }

    private void updateOrInsert(Product product, Quantity quantity, ShoppingCartItem newItem) {
        try {
            ShoppingCartItem foundItem = findItem(product.id());
            foundItem.refresh(product);
            foundItem.changeQuantity(foundItem.quantity().add(quantity));
        } catch (ShoppingCartDoesNotContainProductException e) {
            this.items.add(newItem);
        }
    }

    private void setId(ShoppingCartId id) {
        Objects.requireNonNull(id);
        this.id = id;
    }

    private void setCustomerId(CustomerId customerId) {
        Objects.requireNonNull(customerId);
        this.customerId = customerId;
    }

    private void setTotalAmount(Money totalAmount) {
        Objects.requireNonNull(totalAmount);
        this.totalAmount = totalAmount;
    }

    private void setTotalItems(Quantity totalItems) {
        Objects.requireNonNull(totalItems);
        this.totalItems = totalItems;
    }

    private void setCreatedAt(OffsetDateTime createdAt) {
        Objects.requireNonNull(createdAt);
        this.createdAt = createdAt;
    }

    private void setItems(Set<ShoppingCartItem> items) {
        Objects.requireNonNull(items);
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingCart shoppingCart = (ShoppingCart) o;
        return Objects.equals(id, shoppingCart.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
