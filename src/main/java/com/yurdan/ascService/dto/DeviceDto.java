package com.yurdan.ascService.dto;

import com.yurdan.ascService.model.enums.DeviceColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Это класс DTO, который используется для передачи информации об устройстве между слоями приложения или через API.
 */
//public record DeviceDto(Long id, String name, DeviceColor color) {
//}
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDto {
    private Long id;
    private String name;
    private DeviceColor deviceColor;
    private List<SparePartDto> spareParts;

    public DeviceDto(Long id, String name, DeviceColor deviceColor) {
        this.id = id;
        this.name = name;
        this.deviceColor = deviceColor;
    }
}