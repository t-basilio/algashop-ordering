package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderItem;
import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.domain.model.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.valueobject.Shipping;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.RecipientEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderPersistenceEntityAssembler {

    private final CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    public OrderPersistenceEntity fromDomain(Order order) {
        return merge(new OrderPersistenceEntity(), order);
    }

    public OrderPersistenceEntity merge(OrderPersistenceEntity persistenceEntity, Order order) {
        persistenceEntity.setId(order.id().value().toLong());
        persistenceEntity.setTotalAmount(order.totalAmount().value());
        persistenceEntity.setTotalItems(order.totalItems().value());
        persistenceEntity.setStatus(order.status().name());
        persistenceEntity.setPaymentMethod(order.paymentMethod().name());
        persistenceEntity.setPlacedAt(order.placedAt());
        persistenceEntity.setPaidAt(order.paidAt());
        persistenceEntity.setCanceledAt(order.canceledAt());
        persistenceEntity.setReadyAt(order.readyAt());
        persistenceEntity.setVersion(order.version());
        persistenceEntity.setBilling(toBillingEmbeddable(order.billing()));
        persistenceEntity.setShipping(toShippinEmbeddable(order.shipping()));

        Set<OrderItemPersistenceEntity> mergedItems = mergeItems(order, persistenceEntity);
        persistenceEntity.replaceItems(mergedItems);

        var custumerPersistenceEntity = customerPersistenceEntityRepository
                .getReferenceById(order.customerId().value());
        persistenceEntity.setCustomer(custumerPersistenceEntity);

        return persistenceEntity;
    }

    private Set<OrderItemPersistenceEntity> mergeItems(Order order, OrderPersistenceEntity persistenceEntity) {
        Set<OrderItem> newOrUpdatedItems = order.items();

        if (newOrUpdatedItems.isEmpty())
            return new HashSet<>();

        Set<OrderItemPersistenceEntity> existingItems = persistenceEntity.getItems();
        if (existingItems == null || existingItems.isEmpty())
            return newOrUpdatedItems.stream()
                    .map(this::fromDomain)
                    .collect(Collectors.toSet());

        Map<Long, OrderItemPersistenceEntity> existingItemMap = existingItems.stream()
                .collect(Collectors.toMap(OrderItemPersistenceEntity::getId, item -> item));

        return newOrUpdatedItems.stream()
                .map(orderItem -> {
                    OrderItemPersistenceEntity itemPersistence = existingItemMap
                            .getOrDefault(orderItem.id().value().toLong(), new OrderItemPersistenceEntity());

                    return merge(itemPersistence, orderItem);
                }).collect(Collectors.toSet());
    }

    public OrderItemPersistenceEntity fromDomain(OrderItem orderItem) {
        return merge(new OrderItemPersistenceEntity(), orderItem);
    }

    private OrderItemPersistenceEntity merge(OrderItemPersistenceEntity orderItemPersistenceEntity,
                                             OrderItem orderItem) {

        orderItemPersistenceEntity.setId(orderItem.id().value().toLong());
        orderItemPersistenceEntity.setProductId(orderItem.productId().value());
        orderItemPersistenceEntity.setProductName(orderItem.productName().value());
        orderItemPersistenceEntity.setPrice(orderItem.price().value());
        orderItemPersistenceEntity.setQuantity(orderItem.quantity().value());
        orderItemPersistenceEntity.setTotalAmount(orderItem.totalAmount().value());
        return orderItemPersistenceEntity;
    }

    private ShippingEmbeddable toShippinEmbeddable(Shipping shipping) {
        if (shipping == null)
            return null;

        var builder = ShippingEmbeddable.builder()
                .cost(shipping.cost().value())
                .expectedDate(shipping.expectedDate())
                .address(toAddressEmbeddable(shipping.address()));

        if (shipping.recipient() != null) {
            builder.recipient(RecipientEmbeddable.builder()
                    .firstName(shipping.recipient().fullName().firstName())
                    .lastName(shipping.recipient().fullName().lastName())
                    .document(shipping.recipient().document().value())
                    .phone(shipping.recipient().phone().value())
                    .build());
        }
        return builder.build();
    }

    private BillingEmbeddable toBillingEmbeddable(Billing billing) {
        if (billing == null)
            return null;

        return BillingEmbeddable.builder()
                .firstName(billing.fullName().firstName())
                .lastName(billing.fullName().lastName())
                .document(billing.document().value())
                .phone(billing.phone().value())
                .email(billing.email().value())
                .address(toAddressEmbeddable(billing.address()))
                .build();
    }

    private AddressEmbeddable toAddressEmbeddable(Address address) {
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
