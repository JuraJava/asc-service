package com.yurdan.ascService.dto;

import com.yurdan.ascService.model.enums.DeviceColor;

public record DeviceDto(Long id, String name, DeviceColor color) {
}
