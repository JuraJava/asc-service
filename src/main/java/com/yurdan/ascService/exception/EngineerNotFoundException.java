package com.yurdan.ascService.exception;

public class EngineerNotFoundException extends RuntimeException {
    public EngineerNotFoundException (Long performedById) {
        super("Engineer not found with ID: " + performedById);
    }
}