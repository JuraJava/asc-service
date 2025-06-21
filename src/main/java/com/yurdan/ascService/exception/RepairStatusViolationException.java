package com.yurdan.ascService.exception;

import com.yurdan.ascService.model.enums.RepairStatus;

public class RepairStatusViolationException extends IllegalArgumentException {
    public RepairStatusViolationException(RepairStatus repairStatus) {
        super("ENGINEER cannot set status to " + repairStatus);
    }
}
