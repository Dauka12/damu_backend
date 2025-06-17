package com.AFM.AML.exception;

public class CourseCategoryAlreadyExistsException extends Exception{
    public CourseCategoryAlreadyExistsException() {
    }

    public CourseCategoryAlreadyExistsException(String message) {
        super(message);
    }
}
