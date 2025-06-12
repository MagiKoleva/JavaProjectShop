package org.project.exceptions;

public class ExpiryDateReachedException extends RuntimeException {

    public ExpiryDateReachedException(String message) {
        super(message);
    }
}
