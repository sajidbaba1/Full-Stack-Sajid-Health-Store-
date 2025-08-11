package com.healthstore.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@Audited
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @NotAudited
    private Product product;

    private double price;
    private int stockQuantity;
    private String sku;

    @JsonManagedReference
    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    private List<VariantOption> options = new ArrayList<>();

    // Helper method to add an option
    public void addOption(VariantOption option) {
        options.add(option);
        option.setProductVariant(this);
    }

    // Helper method to remove an option
    public void removeOption(VariantOption option) {
        options.remove(option);
        option.setProductVariant(null);
    }

    // Manual getter methods to ensure compilation works when Lombok fails
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public List<VariantOption> getOptions() {
        return options;
    }

    public void setOptions(List<VariantOption> options) {
        this.options = options;
    }
}
