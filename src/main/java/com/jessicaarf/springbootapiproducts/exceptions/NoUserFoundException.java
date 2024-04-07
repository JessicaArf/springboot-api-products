package com.jessicaarf.springbootapiproducts.exceptions;

public class NoUserFoundException extends RuntimeException{

    public NoUserFoundException(String message){
        super(message);
    }

    public NoUserFoundException(String message, Throwable cause){
        super(message, cause);
    }

}
