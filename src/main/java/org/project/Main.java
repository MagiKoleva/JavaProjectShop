package org.project;

import org.project.data.*;
import org.project.exceptions.NotEnoughQuantityException;
import org.project.exceptions.NotEnoughResourcesException;
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

        // create store
        Store store = new Store(
                "SuperShop",
                BigDecimal.valueOf(20),
                BigDecimal.valueOf(40),
                5,
                BigDecimal.valueOf(10),
                2
        );

        StoreService service = new StoreServiceImpl();
        FileService fileService = new FileServiceImpl();

        // add products
        Product bread = new Product(1, "Bread", BigDecimal.valueOf(1.00),
                ProductCategory.FOOD, LocalDate.now().plusDays(10), BigDecimal.valueOf(10));
        Product milk = new Product(2, "Milk", BigDecimal.valueOf(3.30),
                ProductCategory.FOOD, LocalDate.now().plusDays(5), BigDecimal.valueOf(8));
        Product shampoo = new Product(3, "Shampoo", BigDecimal.valueOf(4.50),
                ProductCategory.NON_FOOD, LocalDate.now().plusMonths(6), BigDecimal.valueOf(5));

        Set<Product> delivery = Set.of(bread, milk, shampoo);
        service.deliverProducts(store, delivery);
        service.setSellingUnitPrices(store);

        System.out.println("--- Available Products ---");
        service.printProducts(store);
        System.out.println();

        // hire cashiers
        service.hireCashiers(store, new Cashier("Ivan",1,  BigDecimal.valueOf(100)));
        service.hireCashiers(store, new Cashier("Maria", 2, BigDecimal.valueOf(110)));

        // create clients
        Client client1 = new Client(1, BigDecimal.valueOf(20));
        client1.setProductsToBuy(Map.of(
                bread, BigDecimal.valueOf(2),
                milk, BigDecimal.valueOf(1)
        ));

        Client client2 = new Client(2, BigDecimal.valueOf(25));
        client2.setProductsToBuy(Map.of(
                bread, BigDecimal.valueOf(2),
                milk, BigDecimal.valueOf(1)
        ));

        Client client3 = new Client(3, BigDecimal.valueOf(30));
        client3.setProductsToBuy(Map.of(
                bread, BigDecimal.valueOf(2),
                milk, BigDecimal.valueOf(1)
        ));

        Client client4 = new Client(4, BigDecimal.valueOf(5));
        client4.setProductsToBuy(Map.of(
                bread, BigDecimal.valueOf(2),
                milk, BigDecimal.valueOf(1)
        ));

        // add clients to store
        store.getClients().addAll(List.of(client1, client2, client3, client4));

        // sell products and issue receipts only if client has money
        int issuedCount = 0;
        int receiptSerial = 1;

        try {
            for (Client client : store.getClients()) {
                Receipt receipt = new Receipt(
                        receiptSerial,
                        store.getCashiers().iterator().next(),
                        LocalDateTime.now()
                );

                service.loadItemsInReceipt(store, client, receipt);
                service.calculateTotalPrice(receipt);

                if (client.enoughMoney(receipt.getTotalPrice())) {
                    service.sellProduct(store, client, receipt);
                    fileService.issueReceipt(receipt, store);
                    System.out.println("Receipt issued for Client " + (issuedCount + 1));
                    issuedCount++;
                } else {
                    throw new NotEnoughResourcesException(
                            "Client does not have enough money! " +
                                    "Required: " + receipt.getTotalPrice() +
                                    ", Available: " + client.getResources()
                    );
                }

                receiptSerial++;
            }
        }  catch (NotEnoughResourcesException e) {
            System.err.println(e.getMessage());
        }

        // summary
        System.out.println("\n--- SUMMARY ---");
        System.out.println("Issued receipts: " + issuedCount);
        System.out.println("Salary expenses: " + service.cashierSalaryExpenses(store));
        System.out.println("Delivery expenses: " + service.deliveryExpenses(store));
        System.out.println("Revenue: " + service.soldProductsIncome(store));
        System.out.println("Profit: " + service.storeProfit(store));

        // deserialize receipts
        System.out.println("\n--- Deserialized Receipts ---");
        for (int i = 1; i <= issuedCount; i++) {
            String fileName = "receipts_ser/receipt_" + i + ".ser"; // ако папката ти е "receipts"
            try {
                Receipt deserializedReceipt = fileService.deserializeReceipt(fileName); // използваме твоя метод!
                System.out.println("Deserialized Receipt #" + i + ":");
                System.out.println(deserializedReceipt);
            } catch (IOException e) {
                System.err.println("IO error: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.err.println("Class not found during deserialization: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Unexpected error, could not deserialize receipt #" + i + ": " + e.getMessage());
            }
        }
    }
}