package com.yurdan.ascService.exception;

public class RepairRequestNotFoundException extends RuntimeException {
    public RepairRequestNotFoundException(Long repairRequestId) {
        super("Repair request not found with ID: " + repairRequestId);
    }
}

