package com.yurdan.ascService.exception;

public class DeviceNotFoundException extends RuntimeException {
    public DeviceNotFoundException(Long deviceId) {
        super("Device not found with ID: " + deviceId);
    }
}

