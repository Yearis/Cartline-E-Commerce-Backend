package com.yearis.e_commerce.exception;

public class SellerNotFoundException extends RuntimeException {
    public SellerNotFoundException(String message) {
        super(message);
    }
}
