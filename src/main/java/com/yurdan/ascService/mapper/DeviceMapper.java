package com.yurdan.ascService.mapper;

import com.yurdan.ascService.dto.DeviceDto;
import com.yurdan.ascService.model.entity.Device;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = SparePartMapper.class)
public interface DeviceMapper {
    DeviceDto toDto(Device device);

    @Mapping(target = "spareParts", ignore = true) // Если маппинг от DTO к Entity не нужен — можно убрать вообще
    Device toEntity(DeviceDto dto);

}