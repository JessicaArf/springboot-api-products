package com.jessicaarf.springbootapiproducts.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoUserFoundException extends RuntimeException{

    public NoUserFoundException(String message){
        super(message);
    }

    public NoUserFoundException(String message, Throwable cause){
        super(message, cause);
    }

}
