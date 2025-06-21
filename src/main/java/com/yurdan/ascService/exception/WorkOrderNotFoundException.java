package com.yurdan.ascService.exception;

public class WorkOrderNotFoundException extends RuntimeException {
    public WorkOrderNotFoundException(Long id) {
        super("WorkOrder not found with id: " + id);
    }
}
