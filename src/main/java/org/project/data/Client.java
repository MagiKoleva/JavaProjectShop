package org.project.data;

import org.project.exceptions.NotEnoughResourcesException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {

    private int id;
    private BigDecimal resources;
    private Map<Product, BigDecimal> productsToBuy; // products and quantity to be bought

    public Client(int id, BigDecimal resources) {
        this.id = id;
        this.resources = resources;
        this.productsToBuy = new HashMap<>();
    }

    public BigDecimal getResources() {
        return resources;
    }

    public void setResources(BigDecimal resources) {
        this.resources = resources;
    }

    public Map<Product, BigDecimal> getProductsToBuy() {
        return productsToBuy;
    }

    public boolean enoughMoney(BigDecimal amount) {
        try {
            if (amount.compareTo(resources) > 0) {
                throw new NotEnoughResourcesException("Not enough money to buy products!");
            }
        } catch (NotEnoughResourcesException e) {
            System.out.println("Caught: " + e.getMessage());
            return false;
        }
        return true;
    }
}
