package org.project.service;

import org.project.data.Cashier;
import org.project.data.Product;
import org.project.data.Store;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public interface StoreService {

    void deliverProducts(Store store, Set<Product> delivery);
    void addProduct(Store store, Product product);

    boolean isProductExpired(Store store, Product product);
    BigDecimal calculateUnitMarkupPrice(Store store, Product product);
    BigDecimal calculateUnitReducedPrice(Store store, BigDecimal price);
    BigDecimal calculateUnitTotalPrice(Store store, Product product);
    void setSellingUnitPrices(Store store);

    boolean hasEnoughProducts(Product product, BigDecimal qtyToSell);
    void reduceProductQuantity(Product product, BigDecimal quantity);
    void sellProduct(Store store, Product product, BigDecimal quantity);

    void hireCashiers(Store store, Cashier cashier);

    BigDecimal cashierSalaryExpenses(Store store);
    BigDecimal deliveryExpenses(Store store);
    BigDecimal soldProductsIncome(Store store);
    BigDecimal storeProfit(Store store);
}
