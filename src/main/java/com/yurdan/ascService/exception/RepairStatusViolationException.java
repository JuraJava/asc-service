package com.yurdan.ascService.exception;

public class RepairStatusViolationException extends IllegalArgumentException {
    public RepairStatusViolationException(String message) {
        super(message);
    }
}
