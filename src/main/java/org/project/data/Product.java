package org.project.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Product {

    private final int id;
    private String name;
    private BigDecimal unitDeliveryPrice;
    private ProductCategory category;
    private LocalDate expirationDate;
    private BigDecimal quantity;

    public Product(int id, String name, BigDecimal unitDeliveryPrice, ProductCategory category, LocalDate expirationDate, BigDecimal quantity) {
        this.id = id;
        this.name = name;
        this.unitDeliveryPrice = unitDeliveryPrice;
        this.category = category;
        this.expirationDate = expirationDate;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getUnitDeliveryPrice() {
        return unitDeliveryPrice;
    }

    public void setUnitDeliveryPrice(BigDecimal unitDeliveryPrice) {
        this.unitDeliveryPrice = unitDeliveryPrice;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", unitDeliveryPrice=" + unitDeliveryPrice +
                ", category=" + category +
                ", expirationDate=" + expirationDate +
                ", quantity=" + quantity +
                '}';
    }
}
