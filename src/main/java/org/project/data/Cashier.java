package org.project.data;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "Cashier{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", salary=" + salary +
                '}';
    }
}
