package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.exception.CantAddLoyaltyPointsToReadyOrder;
import com.algaworks.algashop.ordering.domain.model.exception.OrderNotBelongToCustomerException;
import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.utility.DomainService;
import com.algaworks.algashop.ordering.domain.model.valueobject.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;

import java.util.Objects;

@DomainService
public class CustomerLoyaltyPointsService {

    private static final LoyaltyPoints basePoints = new LoyaltyPoints(5);

    private static final Money expectedAmountToGivePoints = new Money("1000");

    public void addPoints(Customer customer, Order order) {
        Objects.requireNonNull(customer);
        Objects.requireNonNull(order);

        if (!customer.id().equals(order.customerId()))
            throw new OrderNotBelongToCustomerException();

        if (!order.isReady())
            throw new CantAddLoyaltyPointsToReadyOrder();

        customer.addLoyaltyPoints(calculatePoints(order));
    }

    private LoyaltyPoints calculatePoints(Order order) {
        if (shouldGivePointsByAmount(order.totalAmount())) {
            Money result = order.totalAmount().divide(expectedAmountToGivePoints);
            return new LoyaltyPoints(result.value().intValue() * basePoints.value());
        }

        return LoyaltyPoints.ZERO;
    }

    private boolean shouldGivePointsByAmount(Money amount) {
        return amount.compareTo(expectedAmountToGivePoints) >= 0;
    }
}
