package org.project.data;

import org.project.Cashier;
import org.project.Client;
import org.project.Receipt;

import java.math.BigDecimal;
import java.util.*;

public class Store {

    private String name;
    private Set<Product> products; // the products available in the store
    private EnumMap<ProductCategory, BigDecimal> markupPercentages;
    //private BigDecimal markupPercentage;
    private int maxDaysUntilExpiration;
    private BigDecimal reduceByPercentage;
    private Map<Product, BigDecimal> sellingPrices;

    private int cashRegisters;
    private Set<Cashier> cashiers;

    private Set<Product> deliveredProducts;
    private Map<Product, BigDecimal> soldProducts; // the product and its quantity
    //private Set<Product> soldProducts;

    private Queue<Client> clients;
    private Set<Receipt> issuedReceipts;

    public Store(String name, BigDecimal markupFood, BigDecimal markupNonFood, int maxDaysUntilExpiration, BigDecimal reduceByPercentage, int cashRegisters) {
        this.name = name;
        products = new HashSet<>();

        markupPercentages = new EnumMap<>(ProductCategory.class);
        markupPercentages.put(ProductCategory.FOOD, markupFood);
        markupPercentages.put(ProductCategory.NON_FOOD, markupNonFood);

        this.maxDaysUntilExpiration = maxDaysUntilExpiration;
        this.reduceByPercentage = reduceByPercentage;
        this.cashRegisters = cashRegisters;

        cashiers = new HashSet<>();
        deliveredProducts = new HashSet<>();
        soldProducts = new HashMap<>();
        clients = new LinkedList<>();
        issuedReceipts = new HashSet<>();
    }
}
