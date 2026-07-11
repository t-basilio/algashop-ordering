package com.algaworks.algashop.ordering.domain.model.repository;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.entity.CustomerVersionSynchronizer;
import com.algaworks.algashop.ordering.domain.model.valueobject.Email;
import com.algaworks.algashop.ordering.domain.model.valueobject.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.infrastructure.persistence.HibernateConfiguration;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.provider.CustomersPersistenceProvider;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({
        CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class,
        CustomerVersionSynchronizer.class,
        HibernateConfiguration.class
})
class CustomersIT {

    private final Customers customers;
    private final TransactionTemplate newTransaction;
    private final CustomerPersistenceEntityRepository repository;

    @Autowired
    public CustomersIT(Customers customers, CustomerPersistenceEntityRepository repository,
                       PlatformTransactionManager transactionManager) {
        this.customers = customers;
        this.repository = repository;
        this.newTransaction = new TransactionTemplate(transactionManager);
        this.newTransaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @BeforeEach
    void deleteAllCutomers() {
        repository.deleteAll();
    }

    @Test
    void shouldPersistAndFind() {
        Customer originalCustomer = CustomerTestDataBuilder.brandNewCostumer().build();
        CustomerId customerId = originalCustomer.id();
        customers.add(originalCustomer);

        Optional<Customer> possibleCustomer = customers.ofId(customerId);

        assertThat(possibleCustomer).isPresent();

        Customer savedCustomer = possibleCustomer.get();

        assertThat(savedCustomer).satisfies(
                s -> assertThat(s.id()).isEqualTo(customerId),
                s -> assertThat(s.fullName().firstName()).isEqualTo(originalCustomer.fullName().firstName()),
                s -> assertThat(s.fullName().lastName()).isEqualTo(originalCustomer.fullName().lastName()),
                s -> assertThat(s.birthDate()).isEqualTo(originalCustomer.birthDate()),
                s -> assertThat(s.email()).isEqualTo(originalCustomer.email()),
                s -> assertThat(s.phone()).isEqualTo(originalCustomer.phone()),
                s -> assertThat(s.document()).isEqualTo(originalCustomer.document()),
                s -> assertThat(s.isArchived()).isEqualTo(originalCustomer.isArchived()),
                s -> assertThat(s.registeredAt()).isEqualTo(originalCustomer.registeredAt()),
                s -> assertThat(s.archivedAt()).isEqualTo(originalCustomer.archivedAt()),
                s -> assertThat(s.loyaltyPoints()).isEqualTo(originalCustomer.loyaltyPoints()),
                s -> assertThat(s.address()).isEqualTo(originalCustomer.address()),
                s -> assertThat(s.isPromotionNotificationsAllowed())
                        .isEqualTo(originalCustomer.isPromotionNotificationsAllowed())
        );
    }

    @Test
    void shouldUpdateExistingCustomer() {
        Customer customer = CustomerTestDataBuilder.existingCustomer()
                .loyaltyPoints(new LoyaltyPoints(2)).build();

        customers.add(customer);

        customer = customers.ofId(customer.id()).orElseThrow();
        customer.addLoyaltyPoints(new LoyaltyPoints(2));

        customers.add(customer);
        customer = customers.ofId(customer.id()).orElseThrow();

        Assertions.assertThat(customer.loyaltyPoints()).hasToString("4");
    }

    @Test
    void shouldDemonstrateLostUpdateInSingleTransaction() {
        Customer customer = CustomerTestDataBuilder.existingCustomer()
                .loyaltyPoints(new LoyaltyPoints(2)).build();
        customers.add(customer);

        Customer customer1 = customers.ofId(customer.id()).orElseThrow();
        Customer customer2 = customers.ofId(customer.id()).orElseThrow();

        customer1.addLoyaltyPoints(new LoyaltyPoints(2));
        customers.add(customer1); // lost update

        customer2.addLoyaltyPoints(new LoyaltyPoints(3));
        customers.add(customer2);

        Customer savedCustomer = customers.ofId(customer.id()).orElseThrow();

        Assertions.assertThat(savedCustomer.loyaltyPoints()).isNotEqualTo(new LoyaltyPoints(4));
        Assertions.assertThat(savedCustomer.loyaltyPoints()).isNotEqualTo(new LoyaltyPoints(7));
        Assertions.assertThat(savedCustomer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(5));
    }

    @Test
    void shouldNotAllowStaleUpdates() {
        // T0: insert customer in itself transaction
        CustomerId customerId = inNewTransaction(() -> {
            Customer customer = CustomerTestDataBuilder.existingCustomer()
                    .loyaltyPoints(new LoyaltyPoints(2)).build();
            customers.add(customer);
            return customer.id();
        });

        Assertions.assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
                .isThrownBy(() -> inNewTransaction(() -> {
                    // T1: find customer in itself transaction
                    Customer customer1 = customers.ofId(customerId).orElseThrow();

                    // T2: In another segregate transaction, persist first adding integer 2 to loyaltyPoints
                    inNewTransaction(() -> {
                        Customer customer2 = customers.ofId(customerId).orElseThrow();
                        customer2.addLoyaltyPoints(new LoyaltyPoints(2));
                        customers.add(customer2);
                    });

                    // T1 try to persist adding integer 3 to loyaltyPoints and older version
                    customer1.addLoyaltyPoints(new LoyaltyPoints(3));
                    customers.add(customer1);
                }));

        // Verify that the T2 update prevailed
        Customer savedCustomer = customers.ofId(customerId).orElseThrow();
        Assertions.assertThat(savedCustomer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(4));
        Assertions.assertThat(savedCustomer.loyaltyPoints()).isNotEqualTo(new LoyaltyPoints(5));
    }

    @Test
    void shouldCountExistingCustomers() {
        assertThat(customers.count()).isZero();

        Customer customer1 = CustomerTestDataBuilder.brandNewCostumer().build();
        Customer customer2 = CustomerTestDataBuilder.brandNewCostumer().build();

        customers.add(customer1);
        customers.add(customer2);

        assertThat(customers.count()).isEqualTo(2);
    }

    @Test
    void shouldReturnIfCustomerExist() {
        Customer customer = CustomerTestDataBuilder.brandNewCostumer().build();
        customers.add(customer);

        Assertions.assertThat(customers.exists(customer.id())).isTrue();
        Assertions.assertThat(customers.exists(new CustomerId())).isFalse();
    }

    @Test
    void shouldFindByEmail() {
        Customer customer = CustomerTestDataBuilder.brandNewCostumer().build();
        customers.add(customer);

        Optional<Customer> savedCustomer = customers.ofEmail(customer.email());

        Assertions.assertThat(savedCustomer).isPresent();
    }

    @Test
    void shouldNotFindByEmailIfNoCustomerExistsWithEmail() {
        Optional<Customer> custumer = customers.ofEmail(new Email(UUID.randomUUID() + "@email.com"));
        Assertions.assertThat(custumer).isNotPresent();
    }

    @Test
    void shouldReturnIfEmailIsInUse() {
        Customer customer = CustomerTestDataBuilder.brandNewCostumer().build();
        customers.add(customer);

        Assertions.assertThat(customers.isEmailUnique(customer.email(), customer.id())).isTrue();
        Assertions.assertThat(customers.isEmailUnique(customer.email(), new CustomerId())).isFalse();
        Assertions.assertThat(customers.isEmailUnique(new Email("alex@gmail.com"), new CustomerId())).isTrue();
    }

    private <T> T inNewTransaction(Supplier<T> callback) {
        return newTransaction.execute(status -> callback.get());
    }

    private void inNewTransaction(Runnable callback) {
        newTransaction.executeWithoutResult(status -> callback.run());
    }
}
