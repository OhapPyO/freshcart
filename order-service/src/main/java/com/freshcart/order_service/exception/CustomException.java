package com.freshcart.order_service.exception;

public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super( message );
    }

    public CustomException() {
        super();
    }

    public CustomException(String message, Throwable cause) {
        super( message, cause );
    }

    public CustomException(Throwable cause) {
        super( cause );
    }
}
