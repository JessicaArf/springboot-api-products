package com.jessicaarf.springbootapiproducts.exceptions;

public class AuthorizationException extends RuntimeException {
    public AuthorizationException() {
        super("User not authorized.");
    }
    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
