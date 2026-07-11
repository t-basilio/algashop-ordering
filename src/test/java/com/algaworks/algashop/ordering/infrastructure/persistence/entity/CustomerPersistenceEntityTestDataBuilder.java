package com.algaworks.algashop.ordering.infrastructure.persistence.entity;

import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity.CustomerPersistenceEntityBuilder;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static com.algaworks.algashop.ordering.domain.model.entity.CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

public class CustomerPersistenceEntityTestDataBuilder {

    private CustomerPersistenceEntityTestDataBuilder() {}

    public static CustomerPersistenceEntityBuilder aCustomer() {
        return CustomerPersistenceEntity.builder()
                .id(DEFAULT_CUSTOMER_ID.value())
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1989, 11, 21))
                .email("johndoe@email.com")
                .phone("478-256-2504")
                .document("255-08-0578")
                .promotionNotificationsAllowed(true)
                .archived(false)
                .archivedAt(null)
                .registeredAt(OffsetDateTime.now())
                .loyaltyPoints(0)
                .address(AddressEmbeddable.builder()
                        .street("Bourbon Street")
                        .number("1134")
                        .complement("Apt. 114")
                        .neighborhood("North ville")
                        .city("York")
                        .state("South California")
                        .zipCode("12345")
                        .build());
    }

}
