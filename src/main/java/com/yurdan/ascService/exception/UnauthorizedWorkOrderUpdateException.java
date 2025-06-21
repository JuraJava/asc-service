package com.yurdan.ascService.exception;

public class UnauthorizedWorkOrderUpdateException extends RuntimeException {
    public UnauthorizedWorkOrderUpdateException(String message) {
        super(message);
    }
}
