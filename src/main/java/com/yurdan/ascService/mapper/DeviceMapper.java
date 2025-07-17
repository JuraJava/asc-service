package com.yurdan.ascService.mapper;

import com.yurdan.ascService.dto.DeviceDto;
import com.yurdan.ascService.model.entity.Device;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Этот интерфейс определяет маппинг (преобразование) между:
 * Device — это Entity (сущность, хранимая в базе) и
 * DeviceDto — это DTO, используемый в API.
 * Библиотека MapStruct на основании этого интерфейса автоматически сгенерирует реализацию,
 * которая будет конвертировать объекты туда и обратно.
 */
@Mapper(componentModel = "spring", uses = SparePartMapper.class)
public interface DeviceMapper {
    DeviceDto toDto(Device device);

    @Mapping(target = "spareParts", ignore = true) // Если маппинг от DTO к Entity не нужен — можно убрать вообще
    Device toEntity(DeviceDto dto);

}