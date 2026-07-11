package com.algaworks.algashop.ordering.domain.model.exception;

import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;

public class ShoppingCartDoesNotContainItemException extends DomainException {
    public ShoppingCartDoesNotContainItemException(ShoppingCartId id, ShoppingCartItemId shoppingCartItemId) {
        super(ErrorMessages.ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_ITEM.formatted(id, shoppingCartItemId));
    }
}
