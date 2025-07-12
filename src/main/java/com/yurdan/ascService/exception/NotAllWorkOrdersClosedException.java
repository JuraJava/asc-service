package com.yurdan.ascService.exception;

public class NotAllWorkOrdersClosedException extends RuntimeException {
    public NotAllWorkOrdersClosedException(Long repairRequestId) {
        super("Заявка на ремонт ID = " + repairRequestId + " содержит незакрытые заказ-наряды");
    }
}
