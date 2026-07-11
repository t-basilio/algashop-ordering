package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.repository.VersionSynchronizer;

public class ShoppingCartVersionSynchronizer implements VersionSynchronizer<ShoppingCart> {

    @Override
    public void synchronizeVersion(ShoppingCart domainEntity, Long version) {
        domainEntity.setVersion(version);
    }
}
