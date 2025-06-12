package org.project.service.impl;

import org.project.Main;
import org.project.data.*;
import org.project.exceptions.ExpiryDateReachedException;
import org.project.exceptions.NotEnoughQuantityException;
import org.project.exceptions.NotEnoughResourcesException;
import org.project.service.FileService;
import org.project.service.StoreService;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StoreServiceImpl implements StoreService {
    @Override
    public void deliverProducts(Store store, Set<Product> delivery) {
        for (Product product : delivery) {
            store.getProducts().add(product);

            store.getDeliveredProducts().put(product,
                    store.getDeliveredProducts().getOrDefault(product, BigDecimal.ZERO).add(product.getQuantity()));
        }
    }

    @Override
    public void addProduct(Store store, Product product) {
        store.getProducts().add(product);
    }

    @Override
    public void printProducts(Store store) {
        store.getProducts().stream()
                .forEach(System.out::println);
    }

    public long calculateDaysToExpiry(Product product) {
        return ChronoUnit.DAYS.between(LocalDate.now(), product.getExpirationDate());
    }

    public boolean isDiscountApplicable(Store store, long daysToExpiry) {
        return daysToExpiry <= store.getMaxDaysUntilExpiration();
    }

    public BigDecimal calculateBasePriceWithMarkup(Product product, Store store) {
        BigDecimal markup = store.getMarkupPercentages().get(product.getCategory());

        return product.getUnitDeliveryPrice()
                .multiply(BigDecimal.valueOf(1).add(markup.divide(BigDecimal.valueOf(100))))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal applyDiscountIfNeeded(BigDecimal basePrice, Store store) {
        return basePrice.multiply(BigDecimal.valueOf(1).subtract(store.getReduceByPercentage().divide(BigDecimal.valueOf(100))))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public void setSellingUnitPrices(Store store) {
        for (Product product : store.getProducts()) {
            // check if the product is expired
            try {
                validateProductNotExpired(store, product);

                long daysToExpiry = calculateDaysToExpiry(product);
                BigDecimal basePrice = calculateBasePriceWithMarkup(product, store);

                BigDecimal finalPrice;
                if (isDiscountApplicable(store, daysToExpiry)) {
                    finalPrice = applyDiscountIfNeeded(basePrice, store);
                } else {
                    finalPrice = basePrice;
                }

                store.getSellingPrices().put(product, finalPrice);
            } catch (ExpiryDateReachedException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    @Override
    public void reduceProductQuantity(Product product, BigDecimal quantity) {
        product.setQuantity(product.getQuantity().subtract(quantity));
    }

    @Override
    public void loadItemsInReceipt(Store store, Client client, Receipt receipt) {
        Map<Product, BigDecimal> toBuy = client.getProductsToBuy();
        Map<Product, BigDecimal> prices = store.getSellingPrices();

        for (Product p : toBuy.keySet()) {
            BigDecimal qty = toBuy.get(p);
            BigDecimal price = prices.get(p);

            if (price.compareTo(BigDecimal.ZERO) > 0) {
                Map<BigDecimal, BigDecimal> innerMap = new HashMap<>();
                innerMap.put(qty, price);
                receipt.getProductPriceQty().put(p, innerMap);
            } else {
                System.out.println("No price found for: " + p.getName());
            }
        }
    }

    @Override
    public void calculateTotalPrice(Receipt receipt) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Map.Entry<Product, Map<BigDecimal, BigDecimal>> entry : receipt.getProductPriceQty().entrySet()) {
            Map<BigDecimal, BigDecimal> priceQty = entry.getValue();
            for (Map.Entry<BigDecimal, BigDecimal> pq : priceQty.entrySet()) {
                BigDecimal unitPrice = pq.getKey();
                BigDecimal quantity = pq.getValue();
                BigDecimal total = unitPrice.multiply(quantity);
                totalPrice = totalPrice.add(total);
            }
        }
        receipt.setTotalPrice(totalPrice);
    }

    @Override
    public Receipt sellProduct(Store store, Client client, Receipt receipt) {

        Map<Product, Map<BigDecimal, BigDecimal>> productPriceQtyMap = new HashMap<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (Map.Entry<Product, BigDecimal> entry : client.getProductsToBuy().entrySet()) {
            Product product = entry.getKey();
            BigDecimal quantityToBuy = entry.getValue();

            try {
                // check if the product is expired
                if (product.getExpirationDate().isBefore(LocalDate.now())) {
                    throw new ExpiryDateReachedException(
                            "Product '" + product.getName() + "' is expired! " +
                                    "Expiration date: " + product.getExpirationDate() +
                                    ", Today: " + LocalDate.now()
                    );
                }
                // check if there is enough quantity
                if (product.getQuantity().compareTo(quantityToBuy) < 0) {
                    throw new NotEnoughQuantityException(
                            "Product '" + product.getName() + "' doesn't have enough quantity! " +
                                    "Quantity to buy: " + quantityToBuy +
                                    ", Available quantity: " + product.getQuantity()
                    );
                }
                // reduce quantity
                reduceProductQuantity(product, quantityToBuy);

                // add to receipt map
                BigDecimal unitSellingPrice = store.getSellingPrices().get(product);
                BigDecimal productTotal = unitSellingPrice.multiply(quantityToBuy);

                Map<BigDecimal, BigDecimal> priceQty = new HashMap<>();
                priceQty.put(unitSellingPrice, quantityToBuy);
                productPriceQtyMap.put(product, priceQty);

                totalPrice = totalPrice.add(productTotal);

                // add to soldProducts
                store.getSoldProducts().put(product,
                        store.getSoldProducts().getOrDefault(product, BigDecimal.ZERO).add(quantityToBuy));

            } catch (ExpiryDateReachedException | NotEnoughQuantityException e) {
                System.err.println(e.getMessage());
            }
        }

        // check if the client has enough money
        if (client.getResources().compareTo(totalPrice) < 0) {
            System.err.println(
                    "Client does not have enough money! " +
                            "Required: " + totalPrice +
                            ", Available: " + client.getResources()
            );
            //return receipt; // Връщаме празен/непълен Receipt
        }

        // update client's money
        client.setResources(client.getResources().subtract(totalPrice));

        // update Receipt
        receipt.setProductPriceQty(productPriceQtyMap);
        receipt.setTotalPrice(totalPrice);

        // update Store → receiptsCount and turnover
        store.setReceiptsCount(store.getReceiptsCount() + 1);
        store.setTurnover(store.getTurnover().add(totalPrice));

        // add Receipt to Store
        store.getIssuedReceipts().add(receipt);

        return receipt;
    }

    @Override
    public void hireCashiers(Store store, Cashier cashier) {
        store.getCashiers().add(cashier);
    }

    @Override
    public BigDecimal cashierSalaryExpenses(Store store) {
        return store.getCashiers().stream()
                .map(Cashier::getSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal deliveryExpenses(Store store) {
        return store.getDeliveredProducts().entrySet().stream()
                .map(entry -> {
                    Product product = entry.getKey();
                    BigDecimal quantityDelivered = entry.getValue();
                    BigDecimal unitDeliveryPrice = product.getUnitDeliveryPrice();
                    return unitDeliveryPrice.multiply(quantityDelivered);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal soldProductsIncome(Store store) {
        return store.getSoldProducts().entrySet().stream()
                .map(entry -> {
                    Product product = entry.getKey();
                    BigDecimal quantitySold = entry.getValue();
                    BigDecimal unitSellingPrice = store.getSellingPrices().get(product);
                    return unitSellingPrice.multiply(quantitySold);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal storeProfit(Store store) {
        BigDecimal expenses = cashierSalaryExpenses(store)
                .add(deliveryExpenses(store));
        BigDecimal revenue = soldProductsIncome(store);
        return revenue.subtract(expenses);
    }

    @Override
    public void validateProductNotExpired(Store store, Product product) {
        long daysToExp = ChronoUnit.DAYS.between(LocalDate.now(), product.getExpirationDate());
        if (daysToExp < store.getMaxDaysUntilExpiration()) {
            throw new ExpiryDateReachedException(
                    "Product '" + product.getName() + "' is expired! " +
                            "Expiration date: " + product.getExpirationDate() +
                            ", Today: " + LocalDate.now()
            );
        }
    }

    @Override
    public void validateHasEnoughProducts(Product product, BigDecimal qty) {
        if (product.getQuantity().compareTo(qty) < 0) {
            throw new NotEnoughQuantityException(
                    "Product '" + product.getName() + "' doesn't have enough quantity! " +
                            "Quantity to buy: " + qty +
                            ", Available quantity: " + product.getQuantity()
            );
        }
    }

    @Override
    public void validateClientHasEnoughMoney(Client client, BigDecimal requiredAmount) {
        if (!client.enoughMoney(requiredAmount)) {
            throw new NotEnoughResourcesException(
                    "Client does not have enough money! " +
                            "Required: " + requiredAmount +
                            ", Available: " + client.getResources()
            );
        }
    }
}