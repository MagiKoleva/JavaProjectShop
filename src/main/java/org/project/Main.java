package org.project;

import org.project.data.*;
import org.project.exceptions.NotEnoughQuantityException;
import org.project.service.FileService;
import org.project.service.StoreService;
import org.project.service.impl.FileServiceImpl;
import org.project.service.impl.StoreServiceImpl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        new File("receipts_txt").mkdirs();
        new File("receipts_ser").mkdirs();

        Store store = new Store(
                "Lidl",
                BigDecimal.valueOf(30), // food markup %
                BigDecimal.valueOf(60), // non-food markup %
                3,                      // days before expiry to discount
                BigDecimal.valueOf(20), // discount %
                3                       // number of cash registers
        );

        StoreService storeService = new StoreServiceImpl();
        FileService fileService = new FileServiceImpl();

        // 1. Доставка на продукти
        Product pr1 = new Product(1, "Bread", BigDecimal.valueOf(1.00), ProductCategory.FOOD,
                LocalDate.now().plusDays(4), BigDecimal.valueOf(10));
        Product pr2 = new Product(2, "Toothpaste", BigDecimal.valueOf(2.50), ProductCategory.NON_FOOD,
                LocalDate.now().plusMonths(6), BigDecimal.valueOf(5));
        Product pr3 = new Product(3, "Milk", BigDecimal.valueOf(1.20), ProductCategory.FOOD,
                LocalDate.now().plusDays(1), BigDecimal.valueOf(15));
        Product pr4 = new Product(4, "Meat", BigDecimal.valueOf(10.50), ProductCategory.FOOD,
                LocalDate.now().plusDays(6), BigDecimal.valueOf(30.5));

        Set<Product> products = Set.of(pr1, pr2, pr3, pr4);
        storeService.deliverProducts(store, products);
        storeService.setSellingUnitPrices(store);

        // Печат на продукти (с Stream API)
        System.out.println("--- Available Products ---");
        store.getProducts().stream()
                .sorted(Comparator.comparing(Product::getName))
                .forEach(p -> System.out.printf("%s | Qty: %.2f | Unit price: %.2f%n",
                        p.getName(),
                        p.getQuantity(),
                        store.getSellingPrices().get(p)));

        // 2. Назначаване на касиери
        Cashier cashier1 = new Cashier("Ivan", 1, BigDecimal.valueOf(1000.50));
        Cashier cashier2 = new Cashier("Maria", 2, BigDecimal.valueOf(2000));
        Cashier cashier3 = new Cashier("Sonia", 3, BigDecimal.valueOf(1100.60));
        Cashier cashier4 = new Cashier("Petar", 4, BigDecimal.valueOf(1900.0));

        Set<Cashier> cashiers = Set.of(cashier1, cashier2, cashier3, cashier4);

        int count = 0;
        for (Cashier c : cashiers) {
            if (count == store.getCashRegisters()) break;
            storeService.hireCashiers(store, c);
            count++;
        }

        // 3. Клиенти и покупки
        Client client1 = new Client(1, BigDecimal.valueOf(200));
        Client client2 = new Client(2, BigDecimal.valueOf(90.0));

        client1.getProductsToBuy().put(pr1, BigDecimal.valueOf(4));
        client1.getProductsToBuy().put(pr2, BigDecimal.valueOf(2));

        client2.getProductsToBuy().put(pr3, BigDecimal.valueOf(2));
        client2.getProductsToBuy().put(pr4, BigDecimal.valueOf(10.5));

        store.getClients().add(client1);
        store.getClients().add(client2);

        // 4. Продажба → Receipt → serialize

        int receiptSerialCounter = 1;
        /*try {
            Receipt receipt1 = storeService.sellProduct(store, client1, new Receipt(
                    receiptSerialCounter++,
                    cashier1,
                    LocalDateTime.now()
            ));

            fileService.issueReceipt(receipt1, store);
            System.out.println("Receipt issued for Client 1");

            Receipt deserialized1 = fileService.deserializeReceipt("receipts_ser/receipt_" + (receiptSerialCounter - 1) + ".ser");
            System.out.println("--- Deserialized Receipt Client 1 ---");
            System.out.println(deserialized1);

        } catch (NotEnoughQuantityException e) {
            System.err.println("Not enough quantity: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found during deserialization: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }*/
        try {
            Receipt receipt1 = storeService.sellProduct(store, client1, new Receipt(
                    receiptSerialCounter++,
                    cashier1,
                    LocalDateTime.now()
            ));

            fileService.issueReceipt(receipt1, store);
            System.out.println("Receipt issued for Client 1");

            Receipt deserialized1 = fileService.deserializeReceipt("receipts_ser/receipt_" + (receiptSerialCounter - 1) + ".ser");
            System.out.println("--- Deserialized Receipt Client 1 ---");
            System.out.println(deserialized1);

            Receipt receipt2 = storeService.sellProduct(store, client2, new Receipt(
                    receiptSerialCounter++, cashier2, LocalDateTime.now()
            ));

            fileService.issueReceipt(receipt2, store);
            System.out.println("Receipt issued for Client 2");


// Десериализация на Client 2 receipt
            Receipt deserialized2 = fileService.deserializeReceipt("receipts_ser/receipt_" + (receiptSerialCounter - 1) + ".ser");
            if (receipt2.getProductPriceQty() != null && !receipt2.getProductPriceQty().isEmpty()) {
                System.out.println("--- Deserialized Receipt Client 2 ---");
                System.out.println(deserialized2);
            } else {
                System.out.println("Receipt Client 2 is empty (nothing was sold).");
            }
            /*System.out.println("--- Deserialized Receipt Client 2 ---");
            System.out.println(deserialized2);*/

        } catch (NotEnoughQuantityException e) {
            System.err.println("Not enough quantity: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found during deserialization: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }

        System.out.println("Salary expenses: " + storeService.cashierSalaryExpenses(store));
        System.out.println("Delivery expenses: " + storeService.deliveryExpenses(store));
        System.out.println("Income from sold products: " + storeService.soldProductsIncome(store));
        System.out.println("Profit: " + storeService.storeProfit(store));
        System.out.println("Issued receipts count: " + store.getReceiptsCount());
        System.out.println("Total turnover: " + store.getTurnover());
        /*try {
            Receipt receipt1 = storeService.sellProduct(store, client1, new Receipt(
                    receiptSerial,
                    cashier1,
                    LocalDateTime.now()
            ));

            fileService.issueReceipt(receipt1, store);
            System.out.println("Receipt issued for Client 1");

        } catch (NotEnoughQuantityException e) {
            System.err.println(e.getMessage());
        }*/

        // Десериализация
        /*Receipt deserialized = fileService.deserializeReceipt("receipts_ser/receipt_" + receiptSerial + ".ser");
        System.out.println("--- Deserialized Receipt ---");
        System.out.println(deserialized);*/

        // 5. Финансов отчет
        /*System.out.println("Salary expenses: " + storeService.cashierSalaryExpenses(store));
        System.out.println("Delivery expenses: " + storeService.deliveryExpenses(store));
        System.out.println("Income from sold products: " + storeService.soldProductsIncome(store));

        BigDecimal profit = storeService.storeProfit(store);
        System.out.println("Profit: " + profit);

        // 6. Брой бележки
        System.out.println("Issued receipts count: " + store.getReceiptsCount());
        System.out.println("Total turnover: " + store.getTurnover());*/
    }











        /*Store store = new Store(
                "Lidl",
                BigDecimal.valueOf(30),
                BigDecimal.valueOf(60),
                3,
                BigDecimal.valueOf(20),
                3
        );

        StoreService storeService = new StoreServiceImpl();
        FileService fileService = new FileServiceImpl();

        Product pr1 = new Product(1, "Bread", BigDecimal.valueOf(1.00), ProductCategory.FOOD,
                LocalDate.now().plusDays(4), BigDecimal.valueOf(10));
        Product pr2 = new Product(2, "Toothpaste", BigDecimal.valueOf(2.50), ProductCategory.NON_FOOD,
                LocalDate.now().plusMonths(6), BigDecimal.valueOf(5));
        Product pr3 = new Product(3, "Milk", BigDecimal.valueOf(1.20), ProductCategory.FOOD,
                LocalDate.now().plusDays(1), BigDecimal.valueOf(15));
        Product pr4 = new Product(4, "Meat", BigDecimal.valueOf(10.50), ProductCategory.FOOD,
                LocalDate.now().plusDays(6), BigDecimal.valueOf(30.5));
        Set<Product> products = Set.of(pr1, pr2, pr3, pr4);
        storeService.printProducts(store);*/

        /*StoreService storeService = new StoreServiceImpl();
        FileService fileService = new FileServiceImpl();

        // 3. Създаваме продукти
        Product pr1 = new Product(1, "Bread", BigDecimal.valueOf(1.00), ProductCategory.FOOD,
                LocalDate.now().plusDays(4), BigDecimal.valueOf(10));
        Product pr2 = new Product(2, "Toothpaste", BigDecimal.valueOf(2.50), ProductCategory.NON_FOOD,
                LocalDate.now().plusMonths(6), BigDecimal.valueOf(5));
        Product pr3 = new Product(3, "Milk", BigDecimal.valueOf(1.20), ProductCategory.FOOD,
                        LocalDate.now().plusDays(1), BigDecimal.valueOf(15));
        Product pr4 = new Product(4, "Meat", BigDecimal.valueOf(10.50), ProductCategory.FOOD,
                LocalDate.now().plusDays(6), BigDecimal.valueOf(30.5));
        Set<Product> products = Set.of(pr1, pr2, pr3, pr4);

        // 4. Доставка
        storeService.deliverProducts(store, products);
        storeService.setSellingUnitPrices(store);

        // 5. Назначаваме касиери
        Cashier cashier1= new Cashier("Ivan", 1, BigDecimal.valueOf(1000.50));
        Cashier cashier2= new Cashier("Maria", 2, BigDecimal.valueOf(2000));
        Cashier cashier3= new Cashier("Sonia", 3, BigDecimal.valueOf(1100.60));
        Cashier cashier4= new Cashier("Petar", 4, BigDecimal.valueOf(1900.0));
        Set<Cashier> cashiers = Set.of(cashier1, cashier2, cashier3, cashier4);

        int count = 0;
        for (Cashier c : cashiers) {
            if (count == store.getCashRegisters()) break;
            storeService.hireCashiers(store, c);
            count++;
        }

        // 6. Симулираме клиент, който купува 2 продукта
        Client client1 = new Client(1, BigDecimal.valueOf(200));
        Client client2 = new Client(2, BigDecimal.valueOf(90.0));
        client1.getProductsToBuy().put(pr1, BigDecimal.valueOf(4));
        client1.getProductsToBuy().put(pr2, BigDecimal.valueOf(6));
        client2.getProductsToBuy().put(pr2, BigDecimal.valueOf(2));
        client2.getProductsToBuy().put(pr3, BigDecimal.valueOf(2));
        client2.getProductsToBuy().put(pr4, BigDecimal.valueOf(10.5));

        store.getClients().add(client1);
        store.getClients().add(client2);

        Receipt receipt1 = new Receipt(1, cashier1, LocalDateTime.now());
        storeService.sellProduct(store, new Receipt(1, cashier1, LocalDateTime.now()));
        Receipt deserialized = fileService.deserializeReceipt("receipt_1.ser");
        System.out.println("Deserialized receipt: " + deserialized);


        // 7. Създаваме Receipt
        *//*Map<Product, Map<BigDecimal, BigDecimal>> map = new HashMap<>();
        map.put(bought1, Map.of(store.getSellingPrices().get(bought1), BigDecimal.valueOf(2)));
        map.put(bought2, Map.of(store.getSellingPrices().get(bought2), BigDecimal.valueOf(1)));

        BigDecimal totalPrice = map.entrySet().stream()
                .flatMap(e -> e.getValue().entrySet().stream()
                        .map(entry -> entry.getKey().multiply(entry.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Receipt receipt = new Receipt(
                new Random().nextInt(10000),
                store.getCashiers().iterator().next(),
                LocalDateTime.now(),
                map,
                totalPrice
        );*//*

        // 8. Записваме я в .txt и сериализираме .ser
        *//*receiptService.issueReceipt(receipt);*//*

        // 9. Зареждаме от .ser и показваме
        *//*Receipt deserialized = receiptService.loadReceipt(receipt.getSerialNumber());
        System.out.println("Десериализирана бележка: " + deserialized);*//*

        // 10. Извеждаме разходи, приходи, печалба
        System.out.println("Salary expenses: " + storeService.cashierSalaryExpenses(store));
        System.out.println("Delivery expenses: " + storeService.deliveryExpenses(store));
        System.out.println("Income from sold products: " + storeService.soldProductsIncome(store));

        BigDecimal profit = storeService.storeProfit(store);
        System.out.println("Profit: " + profit);*/

        // 11. Брой бележки
        //System.out.println("Issued receipts count: " + fileService.get());
    //}
}