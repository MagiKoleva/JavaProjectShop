package org.project.data;

import java.math.BigDecimal;
import java.util.*;

public class Store {

    private String name;
    private Set<Product> products; // the products available in the store
    private EnumMap<ProductCategory, BigDecimal> markupPercentages;

    private int maxDaysUntilExpiration;
    private BigDecimal reduceByPercentage;

    private Map<Product, BigDecimal> sellingPrices;

    private int cashRegisters;
    private Set<Cashier> cashiers;

    private Map<Product, BigDecimal> deliveredProducts;
    private Map<Product, BigDecimal> soldProducts; // the product and the sold quantity

    private Queue<Client> clients;
    private Set<Receipt> issuedReceipts;
    private BigDecimal turnover;
    private int receiptsCount;

    public Store(String name, BigDecimal markupFood, BigDecimal markupNonFood, int maxDaysUntilExpiration,
                 BigDecimal reduceByPercentage, int cashRegisters) {
        this.name = name;
        products = new HashSet<>();

        markupPercentages = new EnumMap<>(ProductCategory.class);
        markupPercentages.put(ProductCategory.FOOD, markupFood);
        markupPercentages.put(ProductCategory.NON_FOOD, markupNonFood);

        this.maxDaysUntilExpiration = maxDaysUntilExpiration;
        this.reduceByPercentage = reduceByPercentage;
        this.cashRegisters = cashRegisters;

        sellingPrices = new HashMap<>();
        cashiers = new HashSet<>();
        deliveredProducts = new HashMap<>();
        soldProducts = new HashMap<>();
        clients = new LinkedList<>();
        issuedReceipts = new HashSet<>();
        this.turnover = BigDecimal.ZERO;
        this.receiptsCount = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public EnumMap<ProductCategory, BigDecimal> getMarkupPercentages() {
        return markupPercentages;
    }

    public void setMarkupPercentages(EnumMap<ProductCategory, BigDecimal> markupPercentages) {
        this.markupPercentages = markupPercentages;
    }

    public int getMaxDaysUntilExpiration() {
        return maxDaysUntilExpiration;
    }

    public void setMaxDaysUntilExpiration(int maxDaysUntilExpiration) {
        this.maxDaysUntilExpiration = maxDaysUntilExpiration;
    }

    public BigDecimal getReduceByPercentage() {
        return reduceByPercentage;
    }

    public void setReduceByPercentage(BigDecimal reduceByPercentage) {
        this.reduceByPercentage = reduceByPercentage;
    }

    public Map<Product, BigDecimal> getSellingPrices() {
        return sellingPrices;
    }

    public void setSellingPrices(Map<Product, BigDecimal> sellingPrices) {
        this.sellingPrices = sellingPrices;
    }

    public int getCashRegisters() {
        return cashRegisters;
    }

    public void setCashRegisters(int cashRegisters) {
        this.cashRegisters = cashRegisters;
    }

    public Set<Cashier> getCashiers() {
        return cashiers;
    }

    public void setCashiers(Set<Cashier> cashiers) {
        this.cashiers = cashiers;
    }

    public Map<Product, BigDecimal> getDeliveredProducts() {
        return deliveredProducts;
    }

    public void setDeliveredProducts(Map<Product, BigDecimal> deliveredProducts) {
        this.deliveredProducts = deliveredProducts;
    }

    public Map<Product, BigDecimal> getSoldProducts() {
        return soldProducts;
    }

    public void setSoldProducts(Map<Product, BigDecimal> soldProducts) {
        this.soldProducts = soldProducts;
    }

    public Queue<Client> getClients() {
        return clients;
    }

    public void setClients(Queue<Client> clients) {
        this.clients = clients;
    }

    public Set<Receipt> getIssuedReceipts() {
        return issuedReceipts;
    }

    public void setIssuedReceipts(Set<Receipt> issuedReceipts) {
        this.issuedReceipts = issuedReceipts;
    }

    public int getReceiptsCount() {
        return receiptsCount;
    }

    public void setReceiptsCount(int receiptsCount) {
        this.receiptsCount = receiptsCount;
    }

    public BigDecimal getTurnover() {
        return turnover;
    }

    public void setTurnover(BigDecimal turnover) {
        this.turnover = turnover;
    }
}
