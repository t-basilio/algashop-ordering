package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.valueobject.BirthDate;
import com.algaworks.algashop.ordering.domain.model.valueobject.Document;
import com.algaworks.algashop.ordering.domain.model.valueobject.Email;
import com.algaworks.algashop.ordering.domain.model.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.model.valueobject.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.valueobject.Phone;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerPersistenceEntityDisassemblerTest {

    private final CustomerPersistenceEntityDisassembler disassembler = new CustomerPersistenceEntityDisassembler();

    @Test
    void shouldConvertFromPersistence() {
        CustomerPersistenceEntity persistenceEntity = CustomerPersistenceEntityTestDataBuilder.aCustomer()
                .build();

        Customer domainEntity = disassembler.toDomainEntity(persistenceEntity);

        assertThat(domainEntity).satisfies(
                d -> assertThat(d.id()).isEqualTo(new CustomerId(persistenceEntity.getId())),
                d -> assertThat(d.fullName())
                        .isEqualTo(new FullName(persistenceEntity.getFirstName(), persistenceEntity.getLastName())),
                d -> assertThat(d.birthDate()).isEqualTo(new BirthDate(persistenceEntity.getBirthDate())),
                d -> assertThat(d.email()).isEqualTo(new Email(persistenceEntity.getEmail())),
                d -> assertThat(d.phone()).isEqualTo(new Phone(persistenceEntity.getPhone())),
                d -> assertThat(d.document()).isEqualTo(new Document(persistenceEntity.getDocument())),
                d -> assertThat(d.registeredAt()).isEqualTo((persistenceEntity.getRegisteredAt())),
                d -> assertThat(d.loyaltyPoints())
                        .isEqualTo(new LoyaltyPoints(persistenceEntity.getLoyaltyPoints())),
                d -> assertThat(d.address()).isNotNull()
        );
    }
}
