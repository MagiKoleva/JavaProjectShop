package org.project;

import org.project.data.Product;
import org.project.data.ProductCategory;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Product pr1 = new Product(1, "banana", BigDecimal.valueOf(0.50), ProductCategory.FOOD,
                LocalDate.of(2025, 6, 19), BigDecimal.valueOf(100));
        Product pr2 = new Product(2, "meat", BigDecimal.valueOf(10.40), ProductCategory.FOOD,
                LocalDate.of(2025, 6, 5), BigDecimal.valueOf(20));
        Product pr3 = new Product(3, "soap", BigDecimal.valueOf(1.20), ProductCategory.NON_FOOD,
                LocalDate.of(2025, 12, 19), BigDecimal.valueOf(10));
        Product pr4 = new Product(4, "napkin", BigDecimal.valueOf(0.30), ProductCategory.NON_FOOD,
                LocalDate.of(2027, 6, 19), BigDecimal.valueOf(200));
    }
}