package com.BookFlow.bookflow.utils.customException;

public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException(String message) {
        super(message);
    }

}
