package com.yurdan.ascService.exception;

public class ServiceCenterNotFoundException extends RuntimeException{
    public ServiceCenterNotFoundException(Long serviceCenterId) {
        super("Service center not found with ID: " + serviceCenterId);
    }
}
