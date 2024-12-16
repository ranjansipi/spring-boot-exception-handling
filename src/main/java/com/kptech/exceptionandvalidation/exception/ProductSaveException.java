package com.kptech.exceptionandvalidation.exception;

public class ProductSaveException extends  RuntimeException{

    public ProductSaveException(String message){
        super(message);
    }
}
