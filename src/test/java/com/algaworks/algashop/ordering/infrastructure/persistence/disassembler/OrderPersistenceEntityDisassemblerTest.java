package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;


import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntityTestDataBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class OrderPersistenceEntityDisassemblerTest {

    private final OrderPersistenceEntityDisassembler disassembler = new OrderPersistenceEntityDisassembler();

    @Test
    void shouldConvertFromPersistence() {
        OrderPersistenceEntity persistenceEntity = OrderPersistenceEntityTestDataBuilder.existingOrder()
                .build();

        assertThat(persistenceEntity.getItems()).hasSize(2);

        Order domainEntity = disassembler.toDomainEntity(persistenceEntity);

        assertThat(domainEntity).satisfies(
                d -> assertThat(d.id()).isEqualTo(new OrderId(persistenceEntity.getId())),
                d -> assertThat(d.customerId()).isEqualTo(new CustomerId(persistenceEntity.getCustomerId())),
                d -> assertThat(d.totalAmount()).isEqualTo(new Money(persistenceEntity.getTotalAmount())),
                d -> assertThat(d.totalItems()).isEqualTo(new Quantity(persistenceEntity.getTotalItems())),
                d -> assertThat(d.placedAt()).isEqualTo(persistenceEntity.getPlacedAt()),
                d -> assertThat(d.paidAt()).isEqualTo(persistenceEntity.getPaidAt()),
                d -> assertThat(d.canceledAt()).isEqualTo(persistenceEntity.getCanceledAt()),
                d -> assertThat(d.readyAt()).isEqualTo(persistenceEntity.getReadyAt()),
                d -> assertThat(d.status()).isEqualTo(OrderStatus.valueOf(persistenceEntity.getStatus())),
                d -> assertThat(d.paymentMethod())
                        .isEqualTo(PaymentMethod.valueOf(persistenceEntity.getPaymentMethod()))
        );
    }
}