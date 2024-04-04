package com.github.rafaelfernandes.client.adapter.in.web;

import com.github.rafaelfernandes.common.response.ErrorResponse;
import com.github.rafaelfernandes.client.exception.RestaurantDuplicateException;
import com.github.rafaelfernandes.client.exception.RestaurantNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackageClasses = ClientController.class)
public class ClientExceptionHandler {

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<ErrorResponse> restaurantErrorValidation(ValidationException exception){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler({RestaurantDuplicateException.class})
    public ResponseEntity<ErrorResponse> restaurantErrorValidation(RestaurantDuplicateException exception){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler({RestaurantNotFoundException.class})
    public ResponseEntity<ErrorResponse> restaurantErrorValidation(RestaurantNotFoundException exception){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage()));
    }


    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> restaurantErrorValidation(IllegalArgumentException exception){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }



}
