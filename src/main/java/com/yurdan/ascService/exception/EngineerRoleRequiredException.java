package com.yurdan.ascService.exception;

import com.yurdan.ascService.model.enums.RoleOfEmployee;

public class EngineerRoleRequiredException extends SecurityException {
    public EngineerRoleRequiredException(RoleOfEmployee roleOfEmployee) {
        super("Role " + roleOfEmployee  + " is not role ENGINEER. Only engineer can create WorkOrder");
    }
}