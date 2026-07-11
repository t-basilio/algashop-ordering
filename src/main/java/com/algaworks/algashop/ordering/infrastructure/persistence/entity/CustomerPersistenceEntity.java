package com.algaworks.algashop.ordering.infrastructure.persistence.entity;

import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@ToString(of = "id")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "customer")
public class CustomerPersistenceEntity {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    private String email;
    private String phone;
    private String document;

    private Boolean promotionNotificationsAllowed;
    private Boolean archived;
    private OffsetDateTime registeredAt;
    private OffsetDateTime archivedAt;
    private Integer loyaltyPoints;

    @Embedded
    private AddressEmbeddable address;

    @CreatedBy
    private UUID createdByUserId;
    @LastModifiedBy
    private UUID lastModifiedByUserId;
    @LastModifiedDate
    private OffsetDateTime lastModifiedAt;

    @Version
    private Long version;


    @Builder
    public CustomerPersistenceEntity(UUID id, String firstName, String lastName, LocalDate birthDate, String email,
                                     String phone, String document, Boolean promotionNotificationsAllowed,
                                     Boolean archived, OffsetDateTime registeredAt, OffsetDateTime archivedAt,
                                     Integer loyaltyPoints, AddressEmbeddable address, UUID createdByUserId,
                                     UUID lastModifiedByUserId, OffsetDateTime lastModifiedAt, Long version) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.email = email;
        this.phone = phone;
        this.document = document;
        this.promotionNotificationsAllowed = promotionNotificationsAllowed;
        this.archived = archived;
        this.registeredAt = registeredAt;
        this.archivedAt = archivedAt;
        this.loyaltyPoints = loyaltyPoints;
        this.address = address;
        this.setCreatedByUserId(createdByUserId);
        this.setLastModifiedByUserId(lastModifiedByUserId);
        this.setLastModifiedAt(lastModifiedAt);
        this.setVersion(version);
    }
}
