package org.project.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Receipt implements Serializable {

    private int serialNumber;
    private Cashier cashier;
    private LocalDateTime dateAndTime;
    private Map<Product, Map<BigDecimal, BigDecimal>> productPriceQty;
    private BigDecimal totalPrice;

    public Receipt(int serialNumber, Cashier cashier, LocalDateTime dateAndTime) {
        this.serialNumber = serialNumber;
        this.cashier = cashier;
        this.dateAndTime = dateAndTime;
        this.productPriceQty = productPriceQty != null ? productPriceQty : new LinkedHashMap<>();
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public Cashier getCashier() {
        return cashier;
    }

    public LocalDateTime getDateAndTime() {
        return dateAndTime;
    }

    public Map<Product, Map<BigDecimal, BigDecimal>> getProductPriceQty() {
        return productPriceQty;
    }

    public void setProductPriceQty(Map<Product, Map<BigDecimal, BigDecimal>> productPriceQty) {
        this.productPriceQty = productPriceQty;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Receipt receipt = (Receipt) o;
        return serialNumber == receipt.serialNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(serialNumber);
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "serialNumber=" + serialNumber +
                ", cashier=" + cashier +
                ", dateAndTime=" + dateAndTime +
                ", productPriceQty=" + productPriceQty +
                ", totalPrice=" + totalPrice +
                '}';
    }
}