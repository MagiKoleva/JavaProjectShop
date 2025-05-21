package org.project;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Product {

    private final int id;
    private String name;
    private BigDecimal unitDeliveryPrice;
    private ProductCategory category;
    private LocalDate expirationDate;

    public Product(int id, String name, BigDecimal unitDeliveryPrice, ProductCategory category, LocalDate expirationDate) {
        this.id = id;
        this.name = name;
        this.unitDeliveryPrice = unitDeliveryPrice;
        this.category = category;
        this.expirationDate = expirationDate;
    }
}
