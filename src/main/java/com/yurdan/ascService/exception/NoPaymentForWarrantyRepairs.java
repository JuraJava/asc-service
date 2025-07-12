package com.yurdan.ascService.exception;

public class NoPaymentForWarrantyRepairs extends RuntimeException{
    public NoPaymentForWarrantyRepairs(Long repairRequestId) {
        super("Оплаты по гарантийному ремонту от клиента не требуется. ID заявки: " + repairRequestId);
    }
}
