package com.yurdan.ascService.mapper;

import com.yurdan.ascService.dto.CompletedWorkDto;
import com.yurdan.ascService.model.entity.CompletedWork;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompletedWorkMapper {
    CompletedWorkDto toDto(CompletedWork entity);
}
