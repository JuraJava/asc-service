package com.yurdan.ascService.exception;

public class PaymentTransactionAlreadyExistsException extends RuntimeException {
    public PaymentTransactionAlreadyExistsException(Long repairRequestId) {
        super("Платёжная операция по заявке с ID " + repairRequestId + " уже существует.");
    }
}