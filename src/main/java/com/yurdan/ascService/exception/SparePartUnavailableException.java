package com.yurdan.ascService.exception;

public class SparePartUnavailableException extends RuntimeException {
    public SparePartUnavailableException(String batchNumber) {
        super("Spare part out of stock: " + batchNumber);
    }
}
