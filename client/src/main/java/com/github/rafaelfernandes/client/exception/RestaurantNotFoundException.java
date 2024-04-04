package com.github.rafaelfernandes.client.exception;

public class RestaurantNotFoundException extends RuntimeException{

    private static final String ERROR = "Restaurante(s) não existe!";

    public RestaurantNotFoundException() {
        super(ERROR);
    }

    @Override
    public String getMessage() {
        return ERROR;
    }
}
