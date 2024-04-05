package com.jessicaarf.springbootapiproducts.exceptions;


import org.springframework.http.HttpStatus;

public class FileProcessingException extends RuntimeException {
    private final HttpStatus httpStatus;
    public FileProcessingException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public FileProcessingException(String message, HttpStatus httpStatus, Throwable cause) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
