package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.valueobject.LoyaltyPoints;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerPersistenceEntityAssemblerTest {

    private final CustomerPersistenceEntityAssembler assembler = new CustomerPersistenceEntityAssembler();

    @Test
    void shouldConvertFromDomain() {
        Customer customer = CustomerTestDataBuilder.brandNewCostumer().build();
        CustomerPersistenceEntity persistenceEntity = assembler.fromDomain(customer);

        assertThat(persistenceEntity).satisfies(
                p -> assertThat(p.getId()).isEqualTo(customer.id().value()),
                p -> assertThat(p.getFirstName()).isEqualTo(customer.fullName().firstName()),
                p -> assertThat(p.getLastName()).isEqualTo(customer.fullName().lastName()),
                p -> assertThat(p.getBirthDate()).isEqualTo(customer.birthDate().value()),
                p -> assertThat(p.getEmail()).isEqualTo(customer.email().value()),
                p -> assertThat(p.getPromotionNotificationsAllowed())
                                            .isEqualTo(customer.isPromotionNotificationsAllowed()),
                p -> assertThat(p.getArchived()).isEqualTo(customer.isArchived()),
                p -> assertThat(p.getRegisteredAt()).isEqualTo(customer.registeredAt()),
                p -> assertThat(p.getArchivedAt()).isEqualTo(customer.archivedAt()),
                p -> assertThat(p.getLoyaltyPoints()).isEqualTo(customer.loyaltyPoints().value())
        )
                //assert Address
                .satisfies(
                        p -> assertThat(p.getAddress().getStreet())
                                .isEqualTo(customer.address().street()),
                        p -> assertThat(p.getAddress().getNumber())
                                .isEqualTo(customer.address().number()),
                        p -> assertThat(p.getAddress().getNumber())
                                .isEqualTo(customer.address().number()),
                        p -> assertThat(p.getAddress().getComplement())
                                .isEqualTo(customer.address().complement()),
                        p -> assertThat(p.getAddress().getNeighborhood())
                                .isEqualTo(customer.address().neighborhood()),
                        p -> assertThat(p.getAddress().getCity())
                                .isEqualTo(customer.address().city()),
                        p -> assertThat(p.getAddress().getState())
                                .isEqualTo(customer.address().state()),
                        p -> assertThat(p.getAddress().getZipCode())
                                .isEqualTo(customer.address().zipCode().value())
                );
    }

    @Test
    void givenCustomerWithLoyaltyPoints_whenMerge_thenMergeCorrectly() {
        Customer customer = CustomerTestDataBuilder.existingCustomer()
                .loyaltyPoints(new LoyaltyPoints(5)).build();

        assertThat(customer.loyaltyPoints().value()).isEqualTo(5);

        CustomerPersistenceEntity customerPersistenceEntity = CustomerPersistenceEntityTestDataBuilder
                .aCustomer().build();

        assertThat(customerPersistenceEntity.getLoyaltyPoints()).isZero();

        assembler.merge(customerPersistenceEntity, customer);

        assertThat(customerPersistenceEntity.getLoyaltyPoints()).isNotZero();

        assertThat(customerPersistenceEntity.getLoyaltyPoints())
                .isEqualTo(customer.loyaltyPoints().value());
    }
}
