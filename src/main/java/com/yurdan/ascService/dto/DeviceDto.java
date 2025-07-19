package com.yurdan.ascService.dto;

import com.yurdan.ascService.model.enums.DeviceColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

//public record DeviceDto(Long id, String name, DeviceColor color) {
//}
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDto {
    private Long id;
    private String name;
    private String deviceColor;
    private List<SparePartDto> spareParts;

    public DeviceDto(Long id, String name, DeviceColor deviceColor) {
        this.id = id;
        this.name = name;
        this.deviceColor = deviceColor.toString();
    }
}