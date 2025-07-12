package com.yurdan.ascService.mapper;

import com.yurdan.ascService.dto.SparePartDto;
import com.yurdan.ascService.model.entity.SparePart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SparePartMapper {
    SparePartDto toDto(SparePart sparePart);
    SparePart toEntity(SparePartDto dto); // если нужно
}