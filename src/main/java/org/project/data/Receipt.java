package org.project.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class Receipt implements Serializable {

    private int serialNumber;
    private Cashier cashier;
    private LocalDateTime dateAndTime;
    private Map<Product, Map<BigDecimal, BigDecimal>> productPriceQty;
    private BigDecimal totalPrice;

    private int receiptsCount;
    private BigDecimal turnover;

    public Receipt(int serialNumber, Cashier cashier, LocalDateTime dateAndTime,
                   Map<Product, Map<BigDecimal, BigDecimal>> productPriceQty, BigDecimal totalPrice) {
        this.serialNumber = serialNumber;
        this.cashier = cashier;
        this.dateAndTime = dateAndTime;
        this.productPriceQty = productPriceQty;
        this.totalPrice = totalPrice;
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

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public int getReceiptsCount() {
        return receiptsCount;
    }

    public void setReceiptsCount(int receiptsCount) {
        this.receiptsCount = receiptsCount;
    }

    public BigDecimal getTurnover() {
        return turnover;
    }

    public void setTurnover(BigDecimal turnover) {
        this.turnover = turnover;
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
