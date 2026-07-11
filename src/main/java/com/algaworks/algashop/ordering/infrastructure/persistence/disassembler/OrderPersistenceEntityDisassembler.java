package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderItem;
import com.algaworks.algashop.ordering.domain.model.entity.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.valueobject.*;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderItemId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderPersistenceEntityDisassembler {

    public Order toDomainEntity(OrderPersistenceEntity persistenceEntity) {
       return Order.existing()
               .id(new OrderId(persistenceEntity.getId()))
               .customerId(new CustomerId(persistenceEntity.getCustomerId()))
               .totalAmount(new Money(persistenceEntity.getTotalAmount()))
               .totalItems(new Quantity(persistenceEntity.getTotalItems()))
               .status(OrderStatus.valueOf(persistenceEntity.getStatus()))
               .paymentMethod(PaymentMethod.valueOf(persistenceEntity.getPaymentMethod()))
               .placedAt(persistenceEntity.getPlacedAt())
               .paidAt(persistenceEntity.getPaidAt())
               .canceledAt(persistenceEntity.getCanceledAt())
               .readyAt(persistenceEntity.getReadyAt())
               .version(persistenceEntity.getVersion())
               .billing(toBillingValueObject(persistenceEntity.getBilling()))
               .shipping(toShippingValueObject(persistenceEntity.getShipping()))
               .items(toDomainEntity(persistenceEntity.getItems()))
               .build();
    }

    private Set<OrderItem> toDomainEntity(Set<OrderItemPersistenceEntity> items) {
        return items.stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toSet());
    }

    private OrderItem toDomainEntity(OrderItemPersistenceEntity orderItemPersistence) {
        return OrderItem.existing()
                .id(new OrderItemId(orderItemPersistence.getId()))
                .orderId(new OrderId(orderItemPersistence.getOrderId()))
                .productId(new ProductId(orderItemPersistence.getProductId()))
                .productName(new ProductName(orderItemPersistence.getProductName()))
                .price(new Money(orderItemPersistence.getPrice()))
                .quantity(new Quantity(orderItemPersistence.getQuantity()))
                .totalAmount(new Money(orderItemPersistence.getTotalAmount()))
                .build();
    }

    private Shipping toShippingValueObject(ShippingEmbeddable embeddable) {
        if (embeddable == null)
            return null;

        var builder = Shipping.builder()
                .cost(new Money(embeddable.getCost()))
                .expectedDate(embeddable.getExpectedDate())
                .address(toAddressValueObject(embeddable.getAddress()));

        if (embeddable.getRecipient() != null) {
                builder.recipient(Recipient.builder()
                        .fullName(new FullName(embeddable.getRecipient().getFirstName(),
                                embeddable.getRecipient().getLastName()))
                        .document(new Document(embeddable.getRecipient().getDocument()))
                        .phone(new Phone(embeddable.getRecipient().getPhone()))
                        .build());
        }

        return builder.build();
    }

    private Billing toBillingValueObject(BillingEmbeddable embeddable) {
        if (embeddable == null)
            return null;

        return Billing.builder()
                .fullName(new FullName(embeddable.getFirstName(), embeddable.getLastName()))
                .document(new Document(embeddable.getDocument()))
                .phone(new Phone(embeddable.getPhone()))
                .email(new Email(embeddable.getEmail()))
                .address(toAddressValueObject(embeddable.getAddress()))
                .build();
    }

    private Address toAddressValueObject(AddressEmbeddable embeddable) {
        if (embeddable == null)
            return null;

        return Address.builder()
                .street(embeddable.getStreet())
                .number(embeddable.getNumber())
                .complement(embeddable.getComplement())
                .neighborhood(embeddable.getNeighborhood())
                .city(embeddable.getCity())
                .state(embeddable.getState())
                .zipCode(new ZipCode(embeddable.getZipCode()))
                .build();
    }

}
