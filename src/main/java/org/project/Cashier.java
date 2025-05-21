package org.project;

import java.math.BigDecimal;

public class Cashier {

    private String name;
    private final int id;
    private BigDecimal salary;

    public Cashier(BigDecimal salary, int id, String name) {
        this.salary = salary;
        this.id = id;
        this.name = name;
    }
}
