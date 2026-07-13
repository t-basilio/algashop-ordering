package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCartTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.exception.CustomerAlreadyHaveShoppingCartException;
import com.algaworks.algashop.ordering.domain.model.exception.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.repository.Customers;
import com.algaworks.algashop.ordering.domain.model.repository.ShoppingCarts;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ShoppingServiceTest {

    @Mock
    private Customers customers;

    @Mock
    private ShoppingCarts shoppingCarts;

    @InjectMocks
    private ShoppingService shoppingService;

    @Test
    void givenExistingCustomerAndNoShoppingCart_whenStartShopping_thenReturnNewShoppingCart() {
        CustomerId customerId =CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

        Mockito.when(customers.exists(customerId)).thenReturn(true);
        Mockito.when(shoppingCarts.ofCustomer(customerId)).thenReturn(Optional.empty());

        ShoppingCart newShoppingCart = shoppingService.startShopping(customerId);

        Assertions.assertThat(newShoppingCart.id()).isNotNull();
        Assertions.assertThat(newShoppingCart.customerId()).isEqualTo(customerId);
        Assertions.assertThat(newShoppingCart.isEmpty()).isTrue();

        Mockito.verify(customers).exists(customerId);
        Mockito.verify(shoppingCarts).ofCustomer(customerId);
    }

    @Test
    void givenNonExistingCustomer_whenStartShopping_thenThrowCustomerNotFoundException() {
        CustomerId customerId = new CustomerId();

        Mockito.when(customers.exists(customerId)).thenReturn(false);

        Assertions.assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> shoppingService.startShopping(customerId));

        Mockito.verify(customers).exists(customerId);
    }

    @Test
    void givenCustomerWithShoppingCart_whenStartShopping_thenThrowCustomerAlreadyHaveShoppingCartException() {
        CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
        ShoppingCart existingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customerId).build();

        Mockito.when(customers.exists(customerId)).thenReturn(true);
        Mockito.when(shoppingCarts.ofCustomer(customerId)).thenReturn(Optional.of(existingCart));

        Assertions.assertThatExceptionOfType(CustomerAlreadyHaveShoppingCartException.class)
                .isThrownBy(() -> shoppingService.startShopping(customerId));

        Mockito.verify(customers).exists(customerId);
        Mockito.verify(shoppingCarts).ofCustomer(customerId);
    }
}