package com.showscape.movieservice.exception;

public class MovieAlreadyExistsException extends RuntimeException {
    public MovieAlreadyExistsException(String message) {
        super(message);
    }
}
