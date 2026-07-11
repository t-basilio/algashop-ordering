package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerPersistenceEntityAssembler {

    public CustomerPersistenceEntity fromDomain(Customer customer) {
        return merge(new CustomerPersistenceEntity(), customer);
    }

    public CustomerPersistenceEntity merge(CustomerPersistenceEntity persistenceEntity, Customer customer) {
        persistenceEntity.setId(customer.id().value());
        persistenceEntity.setFirstName(customer.fullName().firstName());
        persistenceEntity.setLastName(customer.fullName().lastName());
        persistenceEntity.setBirthDate(customer.birthDate().value());
        persistenceEntity.setEmail(customer.email().value());
        persistenceEntity.setPhone(customer.phone().value());
        persistenceEntity.setDocument(customer.document().value());
        persistenceEntity.setPromotionNotificationsAllowed(customer.isPromotionNotificationsAllowed());
        persistenceEntity.setArchived(customer.isArchived());
        persistenceEntity.setRegisteredAt(customer.registeredAt());
        persistenceEntity.setArchivedAt(customer.archivedAt());
        persistenceEntity.setLoyaltyPoints(customer.loyaltyPoints().value());
        persistenceEntity.setLoyaltyPoints(customer.loyaltyPoints().value());
        persistenceEntity.setAddress(toAddressEmbeddable(customer.address()));
        persistenceEntity.setVersion(customer.version());

        return persistenceEntity;
    }

    private AddressEmbeddable toAddressEmbeddable(Address address) {
        if (address == null)
            return null;

        return AddressEmbeddable.builder()
                .street(address.street())
                .number(address.number())
                .complement(address.complement())
                .neighborhood(address.neighborhood())
                .city(address.city())
                .state(address.state())
                .zipCode(address.zipCode().value())
                .build();
    }
}
