package org.project.exceptions;

public class NotEnoughResourcesException extends RuntimeException {

    public NotEnoughResourcesException(String message) {
        super(message);
    }
}