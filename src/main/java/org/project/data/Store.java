package org.project.data;

import org.project.Cashier;
import org.project.Client;
import org.project.Receipt;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Store {

    private String name;
    private Set<Product> products;
    private EnumMap<ProductCategory, BigDecimal> markupPercentages;
    //private BigDecimal markupPercentage;
    private int maxDaysUntilExpiration;
    private Map<Product, BigDecimal> sellingPrices;

    private int cashRegisters;
    private Set<Cashier> cashiers;

    private Set<Product> deliveredProducts;
    private Map<Product, BigDecimal> soldProducts;
    //private Set<Product> soldProducts;

    private Queue<Client> clients;

    private Set<Receipt> issuedReceipts;
}
