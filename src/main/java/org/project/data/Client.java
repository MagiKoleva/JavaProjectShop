package org.project.data;

import org.project.exceptions.NotEnoughResourcesException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Client {

    private int id;
    private BigDecimal resources;
    private List<Product> productsToBuy;

    public Client(int id, BigDecimal resources) {
        this.id = id;
        this.resources = resources;
        this.productsToBuy = new ArrayList<>();
    }

    public BigDecimal getResources() {
        return resources;
    }

    public void setResources(BigDecimal resources) {
        this.resources = resources;
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
