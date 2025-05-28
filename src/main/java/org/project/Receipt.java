package org.project;

import org.project.data.Product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

public class Receipt {

    private int serialNumber;
    private Cashier cashier;
    private LocalDateTime dateAndTime;
    private Map<Product, Map<BigDecimal, BigDecimal>> productPriceQty;
    private BigDecimal totalPrice;
}
