package org.project.service;

import org.project.Cashier;
import org.project.data.Product;
import org.project.data.Store;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StoreService {

    Set<Product> deliverProducts(Store store, List<Product> delivery);
    void addProduct(Store store, Product product);
    void reduceProductQuantity(Store store, Product product, int quantity);

    BigDecimal cashierSalaryExpenses(Store store, Set<Cashier> cashiers);
    BigDecimal deliveryExpenses(Store store, Set<Product> deliveredProducts);
    BigDecimal soldProductsIncome(Store store, Map<Product, BigDecimal> soldProducts);
    BigDecimal storeProfit(Store store, BigDecimal revenue, BigDecimal expenses);

    void hireCashiers(Store store);
}
