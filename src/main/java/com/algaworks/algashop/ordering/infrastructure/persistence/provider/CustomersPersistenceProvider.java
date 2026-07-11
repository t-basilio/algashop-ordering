package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.repository.Customers;
import com.algaworks.algashop.ordering.domain.model.repository.VersionSynchronizer;
import com.algaworks.algashop.ordering.domain.model.valueobject.Email;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomersPersistenceProvider implements Customers {

    private final CustomerPersistenceEntityAssembler assembler;
    private final CustomerPersistenceEntityDisassembler disassembler;
    private final CustomerPersistenceEntityRepository persistenceRepository;
    private final VersionSynchronizer<Customer> versionSynchronizer;

    @Override
    public Optional<Customer> ofId(CustomerId customerId) {
        Optional<CustomerPersistenceEntity> possibleEntity = persistenceRepository
                .findById(customerId.value());

        return possibleEntity.map(disassembler::toDomainEntity);
    }

    @Override
    public boolean exists(CustomerId customerId) {
        return persistenceRepository.existsById(customerId.value());
    }

    @Override
    public long count() {
        return persistenceRepository.count();
    }

    @Override
    @Transactional(readOnly = false)
    public void add(Customer aggregateRoot) {
        UUID id = aggregateRoot.id().value();

        persistenceRepository.findById(id).ifPresentOrElse(
                persistenceEntity -> update(aggregateRoot, persistenceEntity),
                /* if not present */ () -> insert(aggregateRoot));
    }

    @Override
    public Optional<Customer> ofEmail(Email email) {
        return persistenceRepository.findByEmail(email.value())
                .map(disassembler::toDomainEntity);
    }

    @Override
    public boolean isEmailUnique(Email email, CustomerId exceptCustomerId) {
        return !persistenceRepository.existsByEmailAndIdNot(email.value(), exceptCustomerId.value());
    }

    private void update(Customer aggregateRoot, CustomerPersistenceEntity persistenceEntity) {
        assembler.merge(persistenceEntity, aggregateRoot);
        persistenceRepository.flush(); //need flush to get current version at persistence entity
        versionSynchronizer.synchronizeVersion(aggregateRoot, persistenceEntity.getVersion());
    }

    private void insert(Customer aggregateRoot) {
        CustomerPersistenceEntity persistenceEntity = assembler.fromDomain(aggregateRoot);
        persistenceRepository.saveAndFlush(persistenceEntity);
        versionSynchronizer.synchronizeVersion(aggregateRoot, persistenceEntity.getVersion());
    }
}
