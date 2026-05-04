package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.CustomerArchivedException;
import com.algaworks.algashop.ordering.domain.valueobject.Address;
import com.algaworks.algashop.ordering.domain.valueobject.Email;
import com.algaworks.algashop.ordering.domain.valueobject.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.valueobject.Phone;
import com.algaworks.algashop.ordering.domain.valueobject.ZipCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerTest {

    @Test
    void given_invalidEmail_whenTryToCreateCustomer_ThenGenerateException() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> CustomerTestDataBuilder.brandNewCostumer()
                        .email(new Email("invalid")).build());
    }

    @Test
    void given_invalidEmail_whenTryToUpdatedCustomerEmail_ThenGenerateException() {

        Customer customer = CustomerTestDataBuilder.brandNewCostumer().build();

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.changeEmail(new Email("invalid")));
    }

    @Test
    void given_unarchivedCustomer_whenArchive_ThenAnonymize() {

        Customer customer = CustomerTestDataBuilder.existingCustomer().build();

        customer.archive();

        Assertions.assertWith(customer,
                c -> assertThat(c.fullName()).hasToString("Anonymous Anonymous"),
                c -> assertThat(c.email()).isNotEqualTo(new Email("johndoe@email.com")),
                c -> assertThat(c.phone()).hasToString("000-000-0000"),
                c -> assertThat(c.document()).hasToString("000-00-0000"),
                c -> assertThat(c.birthDate()).isNull(),
                c -> assertThat(c.isPromotionNotificationsAllowed()).isFalse(),
                c -> assertThat(c.address()).isEqualTo(
                        Address.builder()
                                .street("Bourbon Street")
                                .number("Anonymized")
                                .neighborhood("North ville")
                                .city("York")
                                .state("South California")
                                .zipCode(new ZipCode("12345"))
                                .complement(null)
                                .build()
                )
        );
    }

    @Test
    void given_archivedCustomer_whenTryToUpdate_ThenGenerateException() {
        Customer archivedCustomer = CustomerTestDataBuilder.existingAnonymizedCustomer().build();

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(archivedCustomer::archive);

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> archivedCustomer.changeEmail(new Email("email@gmail.com")));

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> archivedCustomer.changePhone(new Phone("123-123-1111")));

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(archivedCustomer::enablePromotionNotifications);

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(archivedCustomer::disablePromotionNotifications);
    }

    @Test
    void given_brandNewCustomer_whenAddLoyaltyPoints_ThenSumPoints() {

        Customer customer = CustomerTestDataBuilder.brandNewCostumer().build();

        customer.addLoyaltyPoints(new LoyaltyPoints(10));
        customer.addLoyaltyPoints(new LoyaltyPoints(20));

        Assertions.assertThat(customer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(30));
    }

    @Test
    void given_brandNewCustomer_whenAddInvalidLoyaltyPoints_ThenGenerateException() {

        Customer customer = CustomerTestDataBuilder.brandNewCostumer().build();

        customer.addLoyaltyPoints(new LoyaltyPoints(10));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.addLoyaltyPoints(LoyaltyPoints.ZERO));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.addLoyaltyPoints(new LoyaltyPoints(-10)));
    }
}