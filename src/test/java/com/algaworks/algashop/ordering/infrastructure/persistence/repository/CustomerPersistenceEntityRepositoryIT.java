package com.algaworks.algashop.ordering.infrastructure.persistence.repository;

import com.algaworks.algashop.ordering.infrastructure.persistence.HibernateConfiguration;
import com.algaworks.algashop.ordering.infrastructure.persistence.config.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({SpringDataAuditingConfig.class, HibernateConfiguration.class})
class CustomerPersistenceEntityRepositoryIT {

    private final CustomerPersistenceEntityRepository persistenceRepository;

    @Autowired
    public CustomerPersistenceEntityRepositoryIT(CustomerPersistenceEntityRepository persistenceRepository) {
       this.persistenceRepository = persistenceRepository;
    }

    @Test
    void shouldPersist() {
        CustomerPersistenceEntity entity = CustomerPersistenceEntityTestDataBuilder.aCustomer().build();

        persistenceRepository.saveAndFlush(entity);

        Assertions.assertThat(persistenceRepository.existsById(entity.getId())).isTrue();

        CustomerPersistenceEntity savedEntity = persistenceRepository.findById(entity.getId()).orElseThrow();

        Assertions.assertThat(savedEntity.getRegisteredAt().truncatedTo(ChronoUnit.MINUTES))
                .isEqualTo(OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES));

    }

    @Test
    void shouldCount() {
        long customerCount = persistenceRepository.count();
        Assertions.assertThat(customerCount).isZero();
    }

    @Test
    void shouldSetAuditingValues() {
        CustomerPersistenceEntity entity = CustomerPersistenceEntityTestDataBuilder.aCustomer().build();
        entity = persistenceRepository.saveAndFlush(entity);

        Assertions.assertThat(entity.getCreatedByUserId()).isNotNull();
        Assertions.assertThat(entity.getLastModifiedByUserId()).isNotNull();
        Assertions.assertThat(entity.getLastModifiedAt()).isNotNull();
    }
}
