package com.dev.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String msg){
        super(msg);
    }
    UserNotFoundException(){
        super("User Not Found!!!");
    }

}