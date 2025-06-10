package org.project.service;

import org.project.data.Cashier;
import org.project.data.Product;
import org.project.data.Store;
import org.project.exceptions.ExpiryDateReachedException;
import org.project.exceptions.NotEnoughQuantityException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;

public class StoreServiceImpl implements StoreService {
    @Override
    public void deliverProducts(Store store, Set<Product> delivery) {
        store.setDeliveredProducts(delivery);

        store.getProducts().addAll(delivery); // add the products to the products set as well
    }

    @Override
    public void addProduct(Store store, Product product) {
        store.getProducts().add(product);
    }

    @Override
    public boolean isProductExpired(Store store, Product product) {
        try {
            long daysToExp = ChronoUnit.DAYS.between(LocalDate.now(), product.getExpirationDate());
            if (daysToExp < store.getMaxDaysUntilExpiration()) {
                throw new ExpiryDateReachedException(
                        "This product is expired!",
                        product.getExpirationDate(),
                        LocalDate.now()
                );
            }
        } catch (ExpiryDateReachedException e) {
            System.out.println("Caught: " + e.getMessage());
            System.out.println("Product " + product.getName() + " can't be sold!");
            return true;
        }
        return false;
    }

    @Override
    public BigDecimal calculateUnitMarkupPrice(Store store, Product product) {
        // base price + markup percentage
        BigDecimal markup = store.getMarkupPercentages().get(product.getCategory());
        BigDecimal base = product.getUnitDeliveryPrice()
                .multiply(BigDecimal.ONE.add(markup.divide(BigDecimal.valueOf(100))));

        return base.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public BigDecimal calculateUnitReducedPrice(Store store, BigDecimal price) {
        // reduce price if the expiry date is close
        BigDecimal reduction = store.getReduceByPercentage();
        BigDecimal reducedPrice = price
                .multiply(BigDecimal.ONE.subtract(reduction.divide(BigDecimal.valueOf(100))));

        return reducedPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public BigDecimal calculateUnitTotalPrice(Store store, Product product) {
        BigDecimal priceWithMarkup = calculateUnitMarkupPrice(store, product);

        if (!isProductExpired(store, product)) {
            BigDecimal newPrice = calculateUnitReducedPrice(store, priceWithMarkup);
            return newPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        return priceWithMarkup.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public void setSellingUnitPrices(Store store) {
        for (Product p : store.getDeliveredProducts()) {
            if (store.getSellingPrices().containsKey(p)) {
                System.out.println("The price for this product is already calculated");
            } else {
                BigDecimal totalPrice = calculateUnitTotalPrice(store, p);
                store.getSellingPrices().put(p, totalPrice);
            }
        }
    }

    /*@Override
    public void setQuantities(Store store, Product product) {
        for (Product p : store.getDeliveredProducts().keySet()) {
            if (store.getQuantities().containsKey(p)) {
                BigDecimal currentValue = store.getQuantities().get(p);
                store.getQuantities().replace(p, currentValue.add(store.getDeliveredProducts().get(p)));
            } else {
                BigDecimal quantity = store.getDeliveredProducts().get(p);
                store.getQuantities().put(p, quantity);
            }
        }
    }*/

    @Override
    public boolean hasEnoughProducts(Product product, BigDecimal qtyToSell) {
        try {
            if (product.getQuantity().compareTo(qtyToSell) < 0) {
                throw new NotEnoughQuantityException(
                        "Not enough quantity!",
                        product,
                        qtyToSell
                );
            }
        } catch (NotEnoughQuantityException e) {
            System.out.println("Caught: " + e.getMessage());
            System.out.println("Product " + product.getName() + " can't be sold!");
            return false;
        }
        return true;
    }

    @Override
    public void reduceProductQuantity(Product product, BigDecimal quantity) {
        if(hasEnoughProducts(product, quantity)) {
            product.setQuantity(product.getQuantity().subtract(quantity));
        }
    }

    @Override
    public void sellProduct(Store store, Product product, BigDecimal quantity) {
        if (hasEnoughProducts(product, quantity)) {
            store.getSoldProducts().put(product, quantity);
            reduceProductQuantity(product, quantity);
        }
    }

    @Override
    public void hireCashiers(Store store, Cashier cashier) {
        store.getCashiers().add(cashier);
    }

    @Override
    public BigDecimal cashierSalaryExpenses(Store store) {
        BigDecimal expenses = BigDecimal.ZERO;
        for (Cashier cashier : store.getCashiers()) {
            expenses.add(cashier.getSalary());
        }
        return expenses;
    }

    @Override
    public BigDecimal deliveryExpenses(Store store) {
        BigDecimal expenses = BigDecimal.ZERO;
        for (Product p : store.getDeliveredProducts()) {
            expenses.add(p.getUnitDeliveryPrice().multiply(p.getQuantity()));
        }
        return expenses;
    }

    @Override
    public BigDecimal soldProductsIncome(Store store, Map<Product, BigDecimal> soldProducts) {
        return null;
    }

    @Override
    public BigDecimal storeProfit(Store store, BigDecimal revenue, BigDecimal expenses) {
        return null;
    }
}
