package org.project.exceptions;

import org.project.data.Product;

import java.math.BigDecimal;

public class NotEnoughQuantityException extends RuntimeException {

    /*private Product product;
    private BigDecimal quantity;
    private BigDecimal quantityToSell;
    private BigDecimal difference;*/

    public NotEnoughQuantityException(String message) {
        super(message);
    }

    /*public NotEnoughQuantityException(String message, Product product, BigDecimal quantityToSell) {
        super(message);
        this.quantity = product.getQuantity();
        this.quantityToSell = quantityToSell;
        difference = quantityToSell.subtract(quantity);
    }*/

    /*@Override
    public String toString() {
        return "NotEnoughQuantityException{" +
                "product=" + product +
                ", quantity=" + quantity +
                ", quantityToSell=" + quantityToSell +
                ", difference=" + difference +
                "} " + super.toString();
    }*/
}
