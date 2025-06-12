package org.project.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.data.*;
import org.project.exceptions.ExpiryDateReachedException;
import org.project.exceptions.NotEnoughQuantityException;
import org.project.exceptions.NotEnoughResourcesException;
import org.project.service.impl.StoreServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class StoreServiceImplTest {

    private StoreServiceImpl storeService;
    private Store store;
    private Product soonExpiring;
    private Product lowStock;

    @BeforeEach
    void setUp() {
        storeService = new StoreServiceImpl();

        store = new Store(
                "TestStore",
                BigDecimal.valueOf(30),
                BigDecimal.valueOf(50),
                3,
                BigDecimal.valueOf(20),
                2
        );
        soonExpiring = new Product(1, "Yogurt",
                BigDecimal.valueOf(1.0),
                ProductCategory.FOOD,
                LocalDate.now().plusDays(1), // изтича утре
                BigDecimal.valueOf(5));
        lowStock = new Product(2, "Chips",
                BigDecimal.valueOf(2.0),
                ProductCategory.NON_FOOD,
                LocalDate.now().plusMonths(6),
                BigDecimal.valueOf(1));
    }

    @Test
    void testHireCashiers() {
        Cashier cashier = new Cashier("Anna", 1, BigDecimal.valueOf(1200));
        storeService.hireCashiers(store, cashier);

        assertTrue(store.getCashiers().contains(cashier));
    }

    @Test
    void testDeliverProducts() {
        Product p = new Product(1, "TestProduct", BigDecimal.valueOf(1.50), ProductCategory.FOOD,
                LocalDate.now().plusDays(5), BigDecimal.valueOf(10));

        storeService.deliverProducts(store, Set.of(p));

        assertTrue(store.getProducts().contains(p));
        assertEquals(BigDecimal.valueOf(10), store.getDeliveredProducts().get(p));
    }

    @Test
    void testSetSellingUnitPricesWithoutDiscount() {
        Product toothpaste = new Product(1, "Toothpaste", BigDecimal.valueOf(2.50), ProductCategory.NON_FOOD,
                LocalDate.now().plusDays(10), BigDecimal.valueOf(5));

        storeService.deliverProducts(store, Set.of(toothpaste));
        storeService.setSellingUnitPrices(store);

        // Expected price: 2.50 * (1 + 0.50) = 3.75 → no discount
        BigDecimal expectedPrice = BigDecimal.valueOf(2.50)
                .multiply(BigDecimal.valueOf(1.50))
                .setScale(2, BigDecimal.ROUND_HALF_UP);

        BigDecimal actualPrice = store.getSellingPrices().get(toothpaste)
                .setScale(2, BigDecimal.ROUND_HALF_UP);

        assertEquals(expectedPrice, actualPrice);
    }

    @Test
    void testSetSellingUnitPricesWithDiscount() {
        Product toothpaste = new Product(1, "Toothpaste", BigDecimal.valueOf(2.50), ProductCategory.NON_FOOD,
                LocalDate.now().plusDays(2), BigDecimal.valueOf(5));

        storeService.deliverProducts(store, Set.of(toothpaste));
        storeService.setSellingUnitPrices(store);

        // Expected price:
        // BasePrice = 2.50 * (1 + 0.50) = 3.75
        // FinalPrice = 3.75 * (1 - 0.20) = 3.00
        BigDecimal expectedBasePrice = BigDecimal.valueOf(2.50)
                .multiply(BigDecimal.valueOf(1.50));

        BigDecimal expectedFinalPrice = expectedBasePrice
                .multiply(BigDecimal.valueOf(0.80))
                .setScale(2, BigDecimal.ROUND_HALF_UP);

        BigDecimal actualPrice = store.getSellingPrices().get(toothpaste)
                .setScale(2, BigDecimal.ROUND_HALF_UP);

        assertEquals(expectedFinalPrice, actualPrice);
    }

    @Test
    void testCashierSalaryExpenses() {
        Cashier c1 = new Cashier("Ivan", 1, BigDecimal.valueOf(1000));
        Cashier c2 = new Cashier("Maria", 2, BigDecimal.valueOf(1500));

        storeService.hireCashiers(store, c1);
        storeService.hireCashiers(store, c2);

        BigDecimal expected = BigDecimal.valueOf(2500);
        assertEquals(expected, storeService.cashierSalaryExpenses(store));
    }

    @Test
    void testDeliveryExpenses() {
        Product p = new Product(1, "Product1", BigDecimal.valueOf(3.00), ProductCategory.FOOD,
                LocalDate.now().plusDays(5), BigDecimal.valueOf(6));

        storeService.deliverProducts(store, Set.of(p));

        BigDecimal expected = BigDecimal.valueOf(18.00);
        assertEquals(expected, storeService.deliveryExpenses(store));
    }

    @Test
    void testSoldProductsIncome() {
        Product p = new Product(1, "Product1", BigDecimal.valueOf(4.00), ProductCategory.NON_FOOD,
                LocalDate.now().plusDays(6), BigDecimal.valueOf(8));

        storeService.deliverProducts(store, Set.of(p));
        storeService.setSellingUnitPrices(store);

        store.getSoldProducts().put(p, BigDecimal.valueOf(2));

        BigDecimal expected = store.getSellingPrices().get(p).multiply(BigDecimal.valueOf(2));
        assertEquals(expected, storeService.soldProductsIncome(store));
    }

    @Test
    void testStoreProfit() {
        Product p = new Product(1, "Product1", BigDecimal.valueOf(2.00), ProductCategory.FOOD,
                LocalDate.now().plusDays(3), BigDecimal.valueOf(8));

        Cashier c = new Cashier("Test", 1, BigDecimal.valueOf(500));
        storeService.hireCashiers(store, c);
        storeService.deliverProducts(store, Set.of(p));
        storeService.setSellingUnitPrices(store);

        store.getSoldProducts().put(p, BigDecimal.valueOf(4));
        BigDecimal income = store.getSellingPrices().get(p).multiply(BigDecimal.valueOf(4));

        store.setTurnover(income);

        BigDecimal expectedProfit = income
                .subtract(storeService.deliveryExpenses(store))
                .subtract(storeService.cashierSalaryExpenses(store));

        assertEquals(expectedProfit, storeService.storeProfit(store));
    }

    @Test
    void integrationTestSellProductWithoutDiscount() {
        // Product with expiry far enough -> no discount
        Product bread = new Product(1, "Bread", BigDecimal.valueOf(1.00), ProductCategory.FOOD,
                LocalDate.now().plusDays(10), BigDecimal.valueOf(20));

        storeService.deliverProducts(store, Set.of(bread));
        storeService.setSellingUnitPrices(store);

        Cashier cashier = new Cashier("Anna", 1, BigDecimal.valueOf(1000));
        storeService.hireCashiers(store, cashier);

        Client client = new Client(1, BigDecimal.valueOf(50));
        client.getProductsToBuy().put(bread, BigDecimal.valueOf(4));

        Receipt receipt = new Receipt(1, cashier, LocalDateTime.now());

        Receipt result = storeService.sellProduct(store, client, receipt);

        BigDecimal expectedTotal = BigDecimal.valueOf(1.00)
                .multiply(BigDecimal.valueOf(1.30)) // markup 30% for food
                .multiply(BigDecimal.valueOf(4))
                .setScale(2, BigDecimal.ROUND_HALF_UP);

        assertEquals(expectedTotal, result.getTotalPrice());
    }

    @Test
    void integrationTestSellProductWithDiscount() {
        // Product with expiry soon -> should get discount
        Product bread = new Product(1, "Bread", BigDecimal.valueOf(1.00), ProductCategory.FOOD,
                LocalDate.now().plusDays(2), BigDecimal.valueOf(20));

        storeService.deliverProducts(store, Set.of(bread));
        storeService.setSellingUnitPrices(store);

        Cashier cashier = new Cashier("Anna", 1, BigDecimal.valueOf(1000));
        storeService.hireCashiers(store, cashier);

        Client client = new Client(2, BigDecimal.valueOf(50));
        client.getProductsToBuy().put(bread, BigDecimal.valueOf(4));

        Receipt receipt = new Receipt(2, cashier, LocalDateTime.now());

        Receipt result = storeService.sellProduct(store, client, receipt);

        BigDecimal expectedUnitPrice = BigDecimal.valueOf(1.00)
                .multiply(BigDecimal.valueOf(1.30)) // markup 30% for food
                .multiply(BigDecimal.valueOf(1).subtract(BigDecimal.valueOf(20).divide(BigDecimal.valueOf(100)))); // discount

        BigDecimal expectedTotal = expectedUnitPrice
                .multiply(BigDecimal.valueOf(4))
                .setScale(2, BigDecimal.ROUND_HALF_UP);

        assertEquals(expectedTotal, result.getTotalPrice());
    }

    @Test
    void integrationTestSellProductWithExpiredItem() {
        Product expired = new Product(2, "Milk", BigDecimal.valueOf(1.50), ProductCategory.FOOD,
                LocalDate.now().minusDays(1), BigDecimal.valueOf(5));

        storeService.deliverProducts(store, Set.of(expired));
        storeService.setSellingUnitPrices(store);

        Cashier cashier = new Cashier("Anna", 1, BigDecimal.valueOf(1000));
        storeService.hireCashiers(store, cashier);

        Client client = new Client(2, BigDecimal.valueOf(100));
        client.getProductsToBuy().put(expired, BigDecimal.valueOf(2));

        Receipt receipt = new Receipt(2, cashier, LocalDateTime.now());

        storeService.sellProduct(store, client, receipt);

        assertTrue(receipt.getProductPriceQty().isEmpty(), "No items should be sold if expired");
    }

    @Test
    void whenProductIsTooCloseToExpiry_thenThrowExpiryDateReachedException() {
        assertThrows(ExpiryDateReachedException.class,
                () -> storeService.validateProductNotExpired(store, soonExpiring));
    }

    @Test
    void whenQuantityIsInsufficient_thenThrowNotEnoughQuantityException() {
        assertThrows(NotEnoughQuantityException.class,
                () -> storeService.validateHasEnoughProducts(lowStock, BigDecimal.valueOf(2)));
    }

    @Test
    void whenClientHasNotEnoughMoney_thenNotEnoughResourcesExceptionIsThrown() {
        Client client = new Client(1, BigDecimal.valueOf(5));

        BigDecimal requiredAmount = BigDecimal.valueOf(20);

        assertThrows(NotEnoughResourcesException.class, () ->
                storeService.validateClientHasEnoughMoney(client, requiredAmount));
    }
}