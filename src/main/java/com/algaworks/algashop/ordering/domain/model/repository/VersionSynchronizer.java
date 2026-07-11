package com.algaworks.algashop.ordering.domain.model.repository;

public interface VersionSynchronizer<T> {
    void synchronizeVersion(T domainEntity, Long version);
}
