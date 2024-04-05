package com.jessicaarf.springbootapiproducts.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ImageAlreadyExistsException extends RuntimeException{

    public ImageAlreadyExistsException(String message){
        super(message);
    }

    public ImageAlreadyExistsException(String message, Throwable cause){
        super(message, cause);
    }

}
