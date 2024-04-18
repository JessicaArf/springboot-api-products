package com.jessicaarf.springbootapiproducts.exceptions;

public class UserAlreadyExistsException extends RuntimeException{

    public UserAlreadyExistsException(){
        super("User with username already exists.");
    }

    public UserAlreadyExistsException(String message){
        super(message);
    }

    public UserAlreadyExistsException(String message, Throwable cause){
        super(message,cause);
    }

}
