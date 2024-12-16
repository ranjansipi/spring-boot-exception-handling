package com.kptech.exceptionandvalidation.exception;

public class ProductIdNotFoundException extends  RuntimeException{

    public ProductIdNotFoundException(String message){
        super(message);
    }
}
