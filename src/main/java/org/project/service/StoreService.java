package org.project.service;

import org.project.data.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public interface StoreService {

    void deliverProducts(Store store, Set<Product> delivery);
    void addProduct(Store store, Product product);
    void printProducts(Store store);

    long calculateDaysToExpiry(Product product);
    boolean isDiscountApplicable(Store store, long daysToExpiry);
    BigDecimal calculateBasePriceWithMarkup(Product product, Store store);
    BigDecimal applyDiscountIfNeeded(BigDecimal basePrice, Store store);
    void setSellingUnitPrices(Store store);

    void reduceProductQuantity(Product product, BigDecimal quantity);
    void loadItemsInReceipt(Store store, Client client, Receipt receipt);
    void calculateTotalPrice(Receipt receipt);
    Receipt sellProduct(Store store, Client client, Receipt receipt);

    void hireCashiers(Store store, Cashier cashier);

    BigDecimal cashierSalaryExpenses(Store store);
    BigDecimal deliveryExpenses(Store store);
    BigDecimal soldProductsIncome(Store store);
    BigDecimal storeProfit(Store store);

    void validateProductNotExpired(Store store, Product product);
    void validateHasEnoughProducts(Product product, BigDecimal qty);
    void validateClientHasEnoughMoney(Client client, BigDecimal requiredAmount);
}