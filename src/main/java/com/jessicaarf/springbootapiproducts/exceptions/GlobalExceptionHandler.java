package com.jessicaarf.springbootapiproducts.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Object> handleProductNotFoundException(ProductNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<Object> handleFileProcessingException(FileProcessingException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }

}
