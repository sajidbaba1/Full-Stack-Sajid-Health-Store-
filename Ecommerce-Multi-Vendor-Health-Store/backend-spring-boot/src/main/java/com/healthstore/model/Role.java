package com.healthstore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true)
    private RoleName name;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<User> users = new HashSet<>();

    public enum RoleName {
        ADMIN,
        USER,
        SELLER,
        PRODUCT_MANAGER,
        SALES_ANALYST,
        CONTENT_MANAGER,
        CUSTOMER_SUPPORT,
        SHIPPING_MANAGER,
        MARKETING_MANAGER,
        SEO_SPECIALIST,
        FINANCE_CONTROLLER,
        PROCUREMENT_OFFICER,
        COUPON_MANAGER
    }
}
