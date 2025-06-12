package org.project.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

public class Receipt implements Serializable {

    private int serialNumber;
    private Cashier cashier;
    private LocalDateTime dateAndTime;
    private Map<Product, Map<BigDecimal, BigDecimal>> productPriceQty;
    private BigDecimal totalPrice;

    /*private int receiptsCount;
    private BigDecimal turnover;*/

    public Receipt(int serialNumber, Cashier cashier, LocalDateTime dateAndTime) {
        this.serialNumber = serialNumber;
        this.cashier = cashier;
        this.dateAndTime = dateAndTime;
        //this.productPriceQty = productPriceQty;
        //this.totalPrice = totalPrice;
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

    /*public int getReceiptsCount() {
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
    }*/

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
        StringBuilder sb = new StringBuilder();
        sb.append("Receipt{serialNumber=").append(serialNumber)
                .append(", cashier=").append(cashier)
                .append(", dateAndTime=").append(dateAndTime);

        if (productPriceQty != null && !productPriceQty.isEmpty()) {
            sb.append(", productPriceQty={");
            for (Map.Entry<Product, Map<BigDecimal, BigDecimal>> entry : productPriceQty.entrySet()) {
                sb.append("\n  ").append(entry.getKey()).append("=");
                for (Map.Entry<BigDecimal, BigDecimal> pq : entry.getValue().entrySet()) {
                    sb.append(String.format("%.2f=%s", pq.getKey(), pq.getValue()));
                }
            }
            sb.append("\n}");
        } else {
            sb.append(", productPriceQty=EMPTY");
        }

        sb.append(", totalPrice=").append(totalPrice)
                .append('}');

        return sb.toString();
    }

    /*@Override
    public String toString() {
        return "Receipt{" +
                "serialNumber=" + serialNumber +
                ", cashier=" + cashier +
                ", dateAndTime=" + dateAndTime +
                ", productPriceQty=" + productPriceQty +
                ", totalPrice=" + totalPrice +
                '}';
    }*/
}
