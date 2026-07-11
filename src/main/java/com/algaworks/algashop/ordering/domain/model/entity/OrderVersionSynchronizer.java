package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.repository.VersionSynchronizer;
import org.springframework.stereotype.Component;

@Component
public class OrderVersionSynchronizer implements VersionSynchronizer<Order> {

    @Override
    public void synchronizeVersion(Order domainEntity, Long version) {
          domainEntity.setVersion(version);
    }
}
