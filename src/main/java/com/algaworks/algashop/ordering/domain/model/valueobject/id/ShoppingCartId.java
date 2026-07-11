package com.algaworks.algashop.ordering.domain.model.valueobject.id;

import com.algaworks.algashop.ordering.domain.model.utility.IdGenerator;
import io.hypersistence.tsid.TSID;

import java.util.Objects;

public record ShoppingCartId(TSID value) {


    public ShoppingCartId {
        Objects.requireNonNull(value);
    }

    public ShoppingCartId() {
        this(IdGenerator.generateTSID());
    }

    public ShoppingCartId(Long value) {
        this(TSID.from(value));
    }

    public ShoppingCartId(String value) {
        this(TSID.from(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
