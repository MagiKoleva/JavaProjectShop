package org.project.exceptions;

import java.time.LocalDate;

public class ExpiryDateReachedException extends RuntimeException {

    private LocalDate expiryDate;
    private LocalDate currentDate;

    public ExpiryDateReachedException(String message, LocalDate expiryDate, LocalDate currentDate) {
        super(message);
        this.expiryDate = expiryDate;
        this.currentDate = currentDate;
    }

    @Override
    public String toString() {
        return "ExpiryDateReachedException{" +
                "expiryDate=" + expiryDate +
                ", currentDate=" + currentDate +
                "} " + super.toString();
    }
}
