package com.algaworks.algashop.ordering.domain.exception;

import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ShoppingCartId;

public class ShoppingCartDoesNotContainProductException extends DomainException {
    public ShoppingCartDoesNotContainProductException(ShoppingCartId id, ProductId productId) {
        super(ErrorMessages.ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_PRODUCT.formatted(id, productId));
    }
}
