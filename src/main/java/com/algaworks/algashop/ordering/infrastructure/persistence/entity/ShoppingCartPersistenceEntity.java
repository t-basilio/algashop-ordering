package com.algaworks.algashop.ordering.infrastructure.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(of = "id")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "shopping_cart")
public class ShoppingCartPersistenceEntity {

    @Id
    @EqualsAndHashCode.Include
    private Long id;
    private BigDecimal totalAmount;
    private Integer totalItems;

    @JoinColumn
    @ManyToOne(optional = false)
    private CustomerPersistenceEntity customer;

    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ShoppingCartItemPersistenceEntity> items = new HashSet<>();

    @CreatedDate
    private OffsetDateTime createdAt;

    @CreatedBy
    private UUID createdByUserId;

    @LastModifiedDate
    private OffsetDateTime lastModifiedAt;

    @LastModifiedBy
    private UUID lastModifiedByUserId;

    @Version
    private Long version;

    @Builder(toBuilder = true)
    public ShoppingCartPersistenceEntity(Long id, CustomerPersistenceEntity customer, BigDecimal totalAmount,
                                         Integer totalItems, OffsetDateTime createdAt, Long version,
                                         Set<ShoppingCartItemPersistenceEntity> items) {
        this.id = id;
        this.customer = customer;
        this.totalAmount = totalAmount;
        this.totalItems = totalItems;
        this.createdAt = createdAt;
        this.version = version;
        this.replaceItems(items);

    }

    public void addItem(ShoppingCartItemPersistenceEntity item) {
        if (item == null)
            return;

        item.setShoppingCart(this);
        this.items.add(item);
    }

    public UUID getCustomerId() {
        if (customer == null)
            return null;

        return this.customer.getId();
    }

    public void replaceItems(Set<ShoppingCartItemPersistenceEntity> updatedItems) {
        if (updatedItems == null)
            this.setItems(new HashSet<>());
        else {
            this.items.clear();

            if (updatedItems.isEmpty())
                return;

            updatedItems.forEach(this::addItem);
        }
    }
}
