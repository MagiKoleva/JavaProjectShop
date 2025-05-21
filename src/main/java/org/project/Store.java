package org.project;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public class Store {

    private String name;
    private Set<Product> products;
    private BigDecimal markupPercentage;
    private int maxDaysUntilExpiration;
    private Map<Product, BigDecimal> sellingPrices;

    private int numberOfCashRegisters;
}
