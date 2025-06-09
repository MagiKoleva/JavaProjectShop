package org.project.service;

import org.project.Cashier;
import org.project.data.Product;
import org.project.data.Store;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StoreServiceImpl implements StoreService {
    @Override
    public Set<Product> deliverProducts(Store store, List<Product> delivery) {
        return Set.of();
    }

    @Override
    public void addProduct(Store store, Product product) {
        store.getProducts().add(product);

        BigDecimal price = calculateUnitPrice(store, product);
        store.getSellingPrices().put(product, price);
    }

    @Override
    public void reduceProductQuantity(Store store, Product product, int quantity) {

    }

    @Override
    public BigDecimal calculateUnitPrice(Store store, Product product) {
        // base price + markup
        BigDecimal markup = store.getMarkupPercentages().get(product.getCategory());
        BigDecimal base = product.getUnitDeliveryPrice()
                .multiply(BigDecimal.ONE.add(markup.divide(BigDecimal.valueOf(100))));

        // check the expiration date
        long daysToExp = ChronoUnit.DAYS.between(LocalDate.now(), product.getExpirationDate());
        if (daysToExp < store.getMaxDaysUntilExpiration()) {
            BigDecimal reduction = store.getReduceByPercentage();
            base = base.multiply(BigDecimal.ONE.subtract(reduction.divide(BigDecimal.valueOf(100))));
        }

        return base.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public BigDecimal cashierSalaryExpenses(Store store, Set<Cashier> cashiers) {
        return null;
    }

    @Override
    public BigDecimal deliveryExpenses(Store store, Set<Product> deliveredProducts) {
        return null;
    }

    @Override
    public BigDecimal soldProductsIncome(Store store, Map<Product, BigDecimal> soldProducts) {
        return null;
    }

    @Override
    public BigDecimal storeProfit(Store store, BigDecimal revenue, BigDecimal expenses) {
        return null;
    }

    @Override
    public void hireCashiers(Store store) {

    }
}
