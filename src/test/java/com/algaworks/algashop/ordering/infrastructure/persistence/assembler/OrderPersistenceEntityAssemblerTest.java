package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderItem;
import com.algaworks.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntityTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OrderPersistenceEntityAssemblerTest {

    @Mock
    private CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    @InjectMocks
    private OrderPersistenceEntityAssembler assembler;

    @BeforeEach
    void setup() {
        Mockito.when(customerPersistenceEntityRepository.getReferenceById(Mockito.any(UUID.class)))
                .then(a -> {
                    UUID customerId = a.getArgument(0, UUID.class);
                    return CustomerPersistenceEntityTestDataBuilder.aCustomer().id(customerId).build();
                });
    }

    @Test
    void shouldConvertFromDomain() {
        Order order = OrderTestDataBuilder.anOrder().build();
        OrderPersistenceEntity orderPersistenceEntity = assembler.fromDomain(order);

        assertThat(orderPersistenceEntity).satisfies(
                p -> assertThat(p.getId()).isEqualTo(order.id().value().toLong()),
                p -> assertThat(p.getCustomerId()).isEqualTo(order.customerId().value()),
                p -> assertThat(p.getTotalAmount()).isEqualTo(order.totalAmount().value()),
                p -> assertThat(p.getTotalItems()).isEqualTo(order.totalItems().value()),
                p -> assertThat(p.getStatus()).isEqualTo(order.status().name()),
                p -> assertThat(p.getPaymentMethod()).isEqualTo(order.paymentMethod().name()),
                p -> assertThat(p.getPlacedAt()).isEqualTo(order.placedAt()),
                p -> assertThat(p.getPaidAt()).isEqualTo(order.paidAt()),
                p -> assertThat(p.getCanceledAt()).isEqualTo(order.canceledAt()),
                p -> assertThat(p.getReadyAt()).isEqualTo(order.readyAt())
        )
                //assert Billing
                .satisfies(
                p -> assertThat(p.getBilling().getFirstName()).isEqualTo(
                        order.billing().fullName().firstName()),
                p -> assertThat(p.getBilling().getLastName()).isEqualTo(
                        order.billing().fullName().lastName()),
                p -> assertThat(p.getBilling().getDocument()).isEqualTo(
                        order.billing().document().value()),
                p -> assertThat(p.getBilling().getPhone()).isEqualTo(
                        order.billing().phone().value()),
                p -> assertThat(p.getBilling().getEmail()).isEqualTo(
                        order.billing().email().value()),
                p -> assertThat(p.getBilling().getAddress().getStreet()).isEqualTo(
                        order.billing().address().street()),
                p -> assertThat(p.getBilling().getAddress().getNumber()).isEqualTo(
                        order.billing().address().number()),
                p -> assertThat(p.getBilling().getAddress().getComplement()).isEqualTo(
                        order.billing().address().complement()),
                p -> assertThat(p.getBilling().getAddress().getNeighborhood()).isEqualTo(
                        order.billing().address().neighborhood()),
                p -> assertThat(p.getBilling().getAddress().getCity()).isEqualTo(
                        order.billing().address().city()),
                p -> assertThat(p.getBilling().getAddress().getState()).isEqualTo(
                        order.billing().address().state()),
                p -> assertThat(p.getBilling().getAddress().getZipCode()).isEqualTo(
                        order.billing().address().zipCode().value())
        )
                //assert Shipping
                .satisfies(
                p -> assertThat(p.getShipping().getCost()).isEqualTo(
                        order.shipping().cost().value()),
                p -> assertThat(p.getShipping().getExpectedDate()).isEqualTo(
                        order.shipping().expectedDate()),
                p -> assertThat(p.getShipping().getRecipient().getFirstName()).isEqualTo(
                        order.shipping().recipient().fullName().firstName()),
                p -> assertThat(p.getShipping().getRecipient().getLastName()).isEqualTo(
                        order.shipping().recipient().fullName().lastName()),
                p -> assertThat(p.getShipping().getRecipient().getDocument()).isEqualTo(
                        order.shipping().recipient().document().value()),
                p -> assertThat(p.getShipping().getRecipient().getPhone()).isEqualTo(
                        order.shipping().recipient().phone().value()),
                p -> assertThat(p.getShipping().getAddress().getStreet()).isEqualTo(
                        order.shipping().address().street()),
                p -> assertThat(p.getShipping().getAddress().getNumber()).isEqualTo(
                        order.shipping().address().number()),
                p -> assertThat(p.getShipping().getAddress().getComplement()).isEqualTo(
                        order.shipping().address().complement()),
                p -> assertThat(p.getShipping().getAddress().getNeighborhood()).isEqualTo(
                        order.shipping().address().neighborhood()),
                p -> assertThat(p.getShipping().getAddress().getCity()).isEqualTo(
                        order.shipping().address().city()),
                p -> assertThat(p.getShipping().getAddress().getState()).isEqualTo(
                        order.shipping().address().state()),
                p -> assertThat(p.getShipping().getAddress().getZipCode()).isEqualTo(
                        order.shipping().address().zipCode().value())
        );
    }

    @Test
    void givenOrderWithNoItems_thenRemovePersistenceEntityItems() {
        Order order = OrderTestDataBuilder.anOrder().withItems(false).build();
        OrderPersistenceEntity orderPersistenceEntity = OrderPersistenceEntityTestDataBuilder.existingOrder().build();

        Assertions.assertThat(order.items()).isEmpty();
        Assertions.assertThat(orderPersistenceEntity.getItems()).isNotEmpty();

        assembler.merge(orderPersistenceEntity, order);

        Assertions.assertThat(orderPersistenceEntity.getItems()).isEmpty();
    }

    @Test
    void givenOrderWithItems_thenAddToPersistenceEntity() {
        Order order = OrderTestDataBuilder.anOrder().withItems(true).build();
        OrderPersistenceEntity orderPersistenceEntity = OrderPersistenceEntityTestDataBuilder
                .existingOrder().items(new HashSet<>()).build();

        Assertions.assertThat(order.items()).isNotEmpty();
        Assertions.assertThat(orderPersistenceEntity.getItems()).isEmpty();

        assembler.merge(orderPersistenceEntity, order);

        Assertions.assertThat(orderPersistenceEntity.getItems()).isNotEmpty();
        Assertions.assertThat(orderPersistenceEntity.getItems()).hasSameSizeAs(order.items());
    }

    @Test
    void givenOrderWithItems_whenMerge_thenMergeCorrectly() {
        Order order = OrderTestDataBuilder.anOrder().withItems(true).build();

        Assertions.assertThat(order.items()).hasSize(2);

        Set<OrderItemPersistenceEntity> orderItemPersistenceEntities = order.items().stream()
                .map(assembler::fromDomain)
                .collect(Collectors.toSet());

        OrderPersistenceEntity orderPersistenceEntity = OrderPersistenceEntityTestDataBuilder.existingOrder()
                .items(orderItemPersistenceEntities)
                .build();

        OrderItem orderItem = order.items().iterator().next();
        order.removeItem(orderItem.id());

        assembler.merge(orderPersistenceEntity, order);

        Assertions.assertThat(orderPersistenceEntity.getItems()).isNotEmpty();
        Assertions.assertThat(orderPersistenceEntity.getItems()).hasSameSizeAs(order.items());
    }
}