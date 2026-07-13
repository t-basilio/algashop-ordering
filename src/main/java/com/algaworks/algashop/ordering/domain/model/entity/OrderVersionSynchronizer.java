package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.repository.VersionSynchronizer;
import com.algaworks.algashop.ordering.domain.model.utility.DomainSynchronizer;

@DomainSynchronizer
public class OrderVersionSynchronizer implements VersionSynchronizer<Order> {

    @Override
    public void synchronizeVersion(Order domainEntity, Long version) {
          domainEntity.setVersion(version);
    }
}
