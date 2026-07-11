package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.entity.CustomerVersionSynchronizer;
import com.algaworks.algashop.ordering.domain.model.valueobject.LoyaltyPoints;
import com.algaworks.algashop.ordering.infrastructure.persistence.HibernateConfiguration;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.config.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@DataJpaTest
@Import({
        CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class,
        CustomerVersionSynchronizer.class,
        CustomersPersistenceProvider.class,
        HibernateConfiguration.class,
        SpringDataAuditingConfig.class
})
class CustomersPersistenceProviderIT {

    private final CustomersPersistenceProvider persistenceProvider;
    private final CustomerPersistenceEntityRepository persistenceEntityRepository;

    @Autowired
    public CustomersPersistenceProviderIT( CustomersPersistenceProvider persistenceProvider,
                                           CustomerPersistenceEntityRepository persistenceEntityRepository) {
        this.persistenceProvider = persistenceProvider;
        this.persistenceEntityRepository = persistenceEntityRepository;
    }

    @Test
    void shouldUpdateAndSumLoyaltyPoints() {
        Customer customer = CustomerTestDataBuilder.brandNewCostumer().build();
        UUID customerId = customer.id().value();
        persistenceProvider.add(customer);

        var persistenceEntity = persistenceEntityRepository.findById(customerId).orElseThrow();

        Assertions.assertThat(persistenceEntity.getLoyaltyPoints()).isZero();
        Assertions.assertThat(persistenceEntity.getCreatedByUserId()).isNotNull();
        Assertions.assertThat(persistenceEntity.getLastModifiedByUserId()).isNotNull();
        Assertions.assertThat(persistenceEntity.getLastModifiedAt()).isNotNull();

        customer = persistenceProvider.ofId(customer.id()).orElseThrow();
        customer.addLoyaltyPoints(new LoyaltyPoints(5));
        persistenceProvider.add(customer);

        persistenceEntity = persistenceEntityRepository.findById(customerId).orElseThrow();

        Assertions.assertThat(persistenceEntity.getLoyaltyPoints()).isEqualTo(5);

        Assertions.assertThat(persistenceEntity.getCreatedByUserId()).isNotNull();
        Assertions.assertThat(persistenceEntity.getLastModifiedByUserId()).isNotNull();
        Assertions.assertThat(persistenceEntity.getLastModifiedAt()).isNotNull();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldAddFindAndNotFailWhenNoTransaction() {
        Customer customer = CustomerTestDataBuilder.brandNewCostumer().build();
        persistenceProvider.add(customer);

        Assertions.assertThatNoException().isThrownBy(
                () -> persistenceProvider.ofId(customer.id()).orElseThrow());
    }

}
