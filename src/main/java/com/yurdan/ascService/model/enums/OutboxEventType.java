package com.yurdan.ascService.model.enums;

public enum OutboxEventType {
    PAYMENT_CREATED,
    PAYMENT_RECEIVED,
    PAYMENT_SUCCEEDED,
    PAYMENT_FAILED,
    PAYMENT_CANCELLED,
    PAYMENT_REJECTED
}
