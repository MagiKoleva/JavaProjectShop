package org.project.service.impl;

import org.project.Main;
import org.project.data.*;
import org.project.exceptions.ExpiryDateReachedException;
import org.project.exceptions.NotEnoughQuantityException;
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

        /*store.setDeliveredProducts(delivery);

        store.getProducts().addAll(delivery);*/ // add the products to the products set as well
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
            if (product.getExpirationDate().isBefore(LocalDate.now())) {
                continue; // skip expired products
            }

            long daysToExpiry = calculateDaysToExpiry(product);
            BigDecimal basePrice = calculateBasePriceWithMarkup(product, store);

            BigDecimal finalPrice;
            if (isDiscountApplicable(store, daysToExpiry)) {
                finalPrice = applyDiscountIfNeeded(basePrice, store);
            } else {
                finalPrice = basePrice;
            }

            store.getSellingPrices().put(product, finalPrice);
        }
    }

   /* @Override
    public void setSellingUnitPrices(Store store) {
        for (Product p : store.getDeliveredProducts().keySet()) {
            if (store.getSellingPrices().containsKey(p)) {
                System.out.println("The price for this product is already calculated");
            } else {
                BigDecimal totalPrice = calculateUnitTotalPrice(store, p);
                store.getSellingPrices().put(p, totalPrice);
            }
        }
    }*/

    @Override
    public boolean hasEnoughProducts(Product product, BigDecimal qtyToSell) {
        try {
            if (product.getQuantity().compareTo(qtyToSell) < 0) {
                throw new NotEnoughQuantityException(
                        "Not enough quantity!"/*,
                        product,
                        qtyToSell*/
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
                // Проверка за expiry
                if (product.getExpirationDate().isBefore(LocalDate.now())) {
                    throw new ExpiryDateReachedException("Product " + product.getName() + " is expired and cannot be sold.");
                }

                // Проверка за количество
                if (product.getQuantity().compareTo(quantityToBuy) < 0) {
                    throw new NotEnoughQuantityException(
                            String.format("Not enough %s: needed %.2f, available %.2f",
                                    product.getName(), quantityToBuy, product.getQuantity()));
                }

                // Намаляване на quantity
                product.setQuantity(product.getQuantity().subtract(quantityToBuy));

                // Добавяне в receipt map
                BigDecimal unitSellingPrice = store.getSellingPrices().get(product);
                BigDecimal productTotal = unitSellingPrice.multiply(quantityToBuy);

                Map<BigDecimal, BigDecimal> priceQty = new HashMap<>();
                priceQty.put(unitSellingPrice, quantityToBuy);
                productPriceQtyMap.put(product, priceQty);

                totalPrice = totalPrice.add(productTotal);

                // Добавяне към soldProducts
                store.getSoldProducts().put(product,
                        store.getSoldProducts().getOrDefault(product, BigDecimal.ZERO).add(quantityToBuy));

            } catch (ExpiryDateReachedException | NotEnoughQuantityException e) {
                // Локално хващаме Exception → пишем съобщение → продължаваме с останалите продукти
                System.err.println(e.getMessage());
            }
        }

        //receipt.setProductPriceQty(new HashMap<>());

        // Проверка дали клиентът има пари
        if (client.getResources().compareTo(totalPrice) < 0) {
            System.err.println("Client does not have enough money! Required: " + totalPrice + ", Available: " + client.getResources());
            return receipt; // Връщаме празен/непълен Receipt
        }

        // Актуализираме парите на клиента
        client.setResources(client.getResources().subtract(totalPrice));

        // Попълваме Receipt
        receipt.setProductPriceQty(productPriceQtyMap);
        receipt.setTotalPrice(totalPrice);

        // Актуализираме Store → receiptsCount и turnover
        store.setReceiptsCount(store.getReceiptsCount() + 1);
        store.setTurnover(store.getTurnover().add(totalPrice));

        // Добавяме Receipt в Store
        store.getIssuedReceipts().add(receipt);

        return receipt;
    }


    /*@Override
    public void sellProduct(Store store, Receipt receipt) {
        for (Client client : store.getClients()) {
            loadItemsInReceipt(store, client, receipt);
            calculateTotalPrice(receipt);

            Map<Product, BigDecimal> toBuy = client.getProductsToBuy();
            BigDecimal totalPrice = receipt.getTotalPrice();

            if (client.enoughMoney(totalPrice)) {
                for (Product p : toBuy.keySet()) {
                    if (hasEnoughProducts(p, toBuy.get(p))) {
                        store.getSoldProducts().put(p, toBuy.get(p));
                        reduceProductQuantity(p, toBuy.get(p));
                    }
                }
            }
            FileService fileService = new FileServiceImpl();
            try {
                fileService.issueReceipt(receipt, store);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }*/

    @Override
    public void hireCashiers(Store store, Cashier cashier) {
        store.getCashiers().add(cashier);
    }

    @Override
    public BigDecimal cashierSalaryExpenses(Store store) {
        return store.getCashiers().stream()
                .map(Cashier::getSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        /*BigDecimal expenses = BigDecimal.ZERO;
        for (Cashier cashier : store.getCashiers()) {
            expenses.add(cashier.getSalary());
        }
        return expenses;*/
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
        /*BigDecimal expenses = BigDecimal.ZERO;
        for (Product p : store.getDeliveredProducts().keySet()) {
            expenses.add(p.getUnitDeliveryPrice().multiply(p.getQuantity()));
        }
        return expenses;*/
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
        /*BigDecimal income = BigDecimal.ZERO;
        for (Product p : store.getSoldProducts().keySet()) {
            income.add(store.getSoldProducts().get(p).multiply(store.getSellingPrices().get(p)));
        }
        return income;*/
    }

    @Override
    public BigDecimal storeProfit(Store store) {
        BigDecimal income = soldProductsIncome(store);
        BigDecimal deliveryExpenses = deliveryExpenses(store);
        BigDecimal salaryExpenses = cashierSalaryExpenses(store);

        return income
                .subtract(deliveryExpenses)
                .subtract(salaryExpenses);
        /*BigDecimal expenses = BigDecimal.ZERO;
        expenses.add(cashierSalaryExpenses(store));
        expenses.add(deliveryExpenses(store));

        BigDecimal revenue = BigDecimal.ZERO;
        revenue.add(soldProductsIncome(store));

        BigDecimal profit = BigDecimal.ZERO;
        profit.add(expenses);
        profit.add(revenue);

        return profit;*/
    }
}
