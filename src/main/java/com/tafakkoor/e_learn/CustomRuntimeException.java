package com.tafakkoor.e_learn;

//@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Endiiiii")
public class CustomRuntimeException extends RuntimeException {
    public CustomRuntimeException(String message) {
        super(message);
    }
}
