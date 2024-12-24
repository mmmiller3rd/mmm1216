package com.tool.rental.exception;

public class InvalidRentalRequestException extends RuntimeException {
    public InvalidRentalRequestException(String exception) {
        super(exception);
    }
}
