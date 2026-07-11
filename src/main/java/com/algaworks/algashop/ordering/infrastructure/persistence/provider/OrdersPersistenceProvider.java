package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.repository.Orders;
import com.algaworks.algashop.ordering.domain.model.repository.VersionSynchronizer;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrdersPersistenceProvider implements Orders {

    private final OrderPersistenceEntityRepository persistenceRepository;
    private final OrderPersistenceEntityAssembler assembler;
    private final OrderPersistenceEntityDisassembler disassembler;
    private final VersionSynchronizer<Order> versionSynchronizer;

    @Override
    public Optional<Order> ofId(OrderId orderId) {
        Optional<OrderPersistenceEntity> possibleEntity = persistenceRepository
                .findById(orderId.value().toLong());

        return possibleEntity.map(disassembler::toDomainEntity);
    }

    @Override
    public boolean exists(OrderId orderId) {
        return persistenceRepository.existsById(orderId.value().toLong());
    }

    @Override
    public long count() {
        return persistenceRepository.count();
    }

    @Override
    @Transactional(readOnly = false)
    public void add(Order aggregateRoot) {
        long orderId = aggregateRoot.id().value().toLong();

        persistenceRepository.findById(orderId)
                .ifPresentOrElse(
                        persistenceEntity -> update(aggregateRoot, persistenceEntity),
                        /* if not present */() -> insert(aggregateRoot));
    }

    @Override
    public List<Order> placedByCustomerInYear(CustomerId customerId, Year year) {
        List<OrderPersistenceEntity> entities = persistenceRepository
                .placedByCustomerInYear(customerId.value(), year.getValue());

        return entities.stream().map(disassembler::toDomainEntity).toList();
    }

    @Override
    public long salesQuantityByCustomerInYear(CustomerId customerId, Year year) {
        return persistenceRepository
                .salesQuantityByCustomerInYear(customerId.value(), year.getValue());
    }

    @Override
    public Money totalSoldForCustomer(CustomerId customerId) {
        return new Money(persistenceRepository.totalSoldForCustomer(customerId.value()));
    }

    private void update(Order aggregateRoot, OrderPersistenceEntity persistenceEntity) {
        assembler.merge(persistenceEntity, aggregateRoot);
        persistenceRepository.flush(); //need flush to get current version at persistence entity
        versionSynchronizer.synchronizeVersion(aggregateRoot, persistenceEntity.getVersion());
    }

    private void insert(Order aggregateRoot) {
        OrderPersistenceEntity persistenceEntity = assembler.fromDomain(aggregateRoot);
        persistenceRepository.saveAndFlush(persistenceEntity);
        versionSynchronizer.synchronizeVersion(aggregateRoot, persistenceEntity.getVersion());
    }

}
