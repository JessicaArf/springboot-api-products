package com.jessicaarf.springbootapiproducts.exceptions;

public class UsersNotFoundException extends RuntimeException{

   public UsersNotFoundException(){
       super("No users found.");
   }

    public UsersNotFoundException(String message){
        super(message);
    }

    public UsersNotFoundException(String message, Throwable cause){
        super(message, cause);
    }

}
