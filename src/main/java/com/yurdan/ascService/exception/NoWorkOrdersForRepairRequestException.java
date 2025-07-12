package com.yurdan.ascService.exception;

public class NoWorkOrdersForRepairRequestException extends RuntimeException {
    public NoWorkOrdersForRepairRequestException(Long repairRequestId) {
        super("Платёжная операция не может быть создана, так как по этой заявке ещё нет заказ-нарядов. ID заявки: " + repairRequestId);
    }
}
