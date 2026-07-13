package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.repository.VersionSynchronizer;
import com.algaworks.algashop.ordering.domain.model.utility.DomainSynchronizer;

@DomainSynchronizer
public class CustomerVersionSynchronizer implements VersionSynchronizer<Customer> {

    @Override
    public void synchronizeVersion(Customer domainEntity, Long version) {
        domainEntity.setVersion(version);
    }
}
