package com.algaworks.algashop.ordering.domain.exception;

import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;

public class ProductOutOfStockException extends DomainException {

    public ProductOutOfStockException(ProductId id) {
        super(ErrorMessages.ERROR_PRODUCT_IS_OUT_OF_STOCK.formatted(id));
    }
}
