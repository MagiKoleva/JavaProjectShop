package org.project.service.impl;

import org.project.data.Product;
import org.project.data.Receipt;
import org.project.data.Store;
import org.project.service.FileService;

import java.io.*;
import java.math.BigDecimal;
import java.util.Map;

public class FileServiceImpl implements FileService {
    @Override
    public void issueReceipt(Receipt receipt, Store store) throws IOException {
        String fileName = "receipts_txt/receipt_" + receipt.getSerialNumber() + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("RECEIPT № " + receipt.getSerialNumber());
            writer.newLine();
            writer.write("Cashier: " + receipt.getCashier().getName());
            writer.newLine();
            writer.write("Date and time: " + receipt.getDateAndTime());
            writer.newLine();
            writer.write("=====================================");
            writer.newLine();

            for (Map.Entry<Product, Map<BigDecimal, BigDecimal>> entry : receipt.getProductPriceQty().entrySet()) {
                Product product = entry.getKey();
                Map<BigDecimal, BigDecimal> priceQty = entry.getValue();
                for (Map.Entry<BigDecimal, BigDecimal> pq : priceQty.entrySet()) {
                    BigDecimal unitPrice = pq.getKey();
                    BigDecimal quantity = pq.getValue();
                    BigDecimal total = unitPrice.multiply(quantity);
                    writer.write(String.format("%-20s x %.2f @ %.2f BGN = %.2f BGN",
                            product.getName(),
                            quantity,
                            unitPrice,
                            total));
                    writer.newLine();
                }
            }
            writer.write("=====================================");
            writer.newLine();
            writer.write("Total: " + receipt.getTotalPrice() + " BGN");
            writer.newLine();
        }

        serializeReceipt(receipt);
    }

    @Override
    public void serializeReceipt(Receipt receipt) throws IOException {
        String fileName = "receipts_ser/receipt_" + receipt.getSerialNumber() + ".ser";
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(receipt);
        }
    }

    @Override
    public Receipt deserializeReceipt(String fileName) throws IOException, ClassNotFoundException {
        Receipt receipt = null;
        try (FileInputStream fileInputStream = new FileInputStream(fileName);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            receipt = (Receipt) objectInputStream.readObject();
        }

        return receipt;
    }
}