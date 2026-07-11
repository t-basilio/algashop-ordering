package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCartItem;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.ShoppingCartPersistenceEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ShoppingCartPersistenceEntityAssembler {

    private final CustomerPersistenceEntityRepository customerPersistenceEntityRepository;
    private final ShoppingCartPersistenceEntityRepository shoppingCartPersistenceEntityRepository;

    public ShoppingCartPersistenceEntity fromDomain(ShoppingCart shoppingCart) {
        return merge(new ShoppingCartPersistenceEntity(), shoppingCart);
    }

    public ShoppingCartPersistenceEntity merge(ShoppingCartPersistenceEntity persistenceEntity,
                                               ShoppingCart shoppingCart) {
        persistenceEntity.setId(shoppingCart.id().value().toLong());
        persistenceEntity.setTotalAmount(shoppingCart.totalAmount().value());
        persistenceEntity.setTotalItems(shoppingCart.totalItems().value());
        persistenceEntity.setCreatedAt(shoppingCart.createdAt());

        Set<ShoppingCartItemPersistenceEntity> mergedItems = mergedItems(shoppingCart, persistenceEntity);
        persistenceEntity.replaceItems(mergedItems);

        var customer = customerPersistenceEntityRepository.getReferenceById(shoppingCart.customerId().value());
        persistenceEntity.setCustomer(customer);

        return persistenceEntity;
    }

    private Set<ShoppingCartItemPersistenceEntity> mergedItems(ShoppingCart shoppingCart,
                                                               ShoppingCartPersistenceEntity persistenceEntity) {
        Set<ShoppingCartItem> newOrUpdatedItems = shoppingCart.items();

        if(newOrUpdatedItems.isEmpty())
            return new HashSet<>();

        Set<ShoppingCartItemPersistenceEntity> existingItems = persistenceEntity.getItems();
        if (existingItems == null || existingItems.isEmpty())
            return newOrUpdatedItems.stream().map(this::fromDomain).collect(Collectors.toSet());

        Map<Long, ShoppingCartItemPersistenceEntity> existingItemMap = existingItems.stream()
                .collect(Collectors.toMap(ShoppingCartItemPersistenceEntity::getId, item -> item));

        return newOrUpdatedItems.stream()
                .map(shoppingCartItem -> {
                    ShoppingCartItemPersistenceEntity itemPersistence = existingItemMap.getOrDefault(
                            shoppingCartItem.id().value().toLong(), new ShoppingCartItemPersistenceEntity()
                    );
                    return merge(itemPersistence, shoppingCartItem);
                }).collect(Collectors.toSet());

    }

    public ShoppingCartItemPersistenceEntity fromDomain(ShoppingCartItem shoppingCartItem) {
        return merge(new ShoppingCartItemPersistenceEntity(), shoppingCartItem);
    }

    private ShoppingCartItemPersistenceEntity merge(ShoppingCartItemPersistenceEntity persistenceEntity,
                                                    ShoppingCartItem shoppingCartItem) {
        persistenceEntity.setId(shoppingCartItem.id().value().toLong());
        persistenceEntity.setProductId(shoppingCartItem.productId().value());
        persistenceEntity.setProductName(shoppingCartItem.productName().value());
        persistenceEntity.setPrice(shoppingCartItem.price().value());
        persistenceEntity.setQuantity(shoppingCartItem.quantity().value());
        persistenceEntity.setTotalAmount(shoppingCartItem.totalAmount().value());
        persistenceEntity.setAvailable(shoppingCartItem.isAvailable());

        var shoppingCart = shoppingCartPersistenceEntityRepository
                .getReferenceById(shoppingCartItem.shoppingCartId().value().toLong());
        persistenceEntity.setShoppingCart(shoppingCart);
        return persistenceEntity;
    }
}
