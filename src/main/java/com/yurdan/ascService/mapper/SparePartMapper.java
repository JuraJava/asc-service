package com.yurdan.ascService.mapper;

import com.yurdan.ascService.dto.SparePartDto;
import com.yurdan.ascService.model.entity.SparePart;
import org.mapstruct.Mapper;

/**
 * Это интерфейс-мэппер, созданный с помощью библиотеки MapStruct,
 * Он предназначен для автоматического преобразования между сущностью SparePart и DTO-объектом SparePartDto
 */
@Mapper(componentModel = "spring")
public interface SparePartMapper {
    SparePartDto toDto(SparePart sparePart);
    SparePart toEntity(SparePartDto dto); // если нужно
}