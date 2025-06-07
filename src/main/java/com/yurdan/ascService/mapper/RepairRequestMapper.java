package com.yurdan.ascService.mapper;

import com.yurdan.ascService.dto.RepairRequestDto;
import com.yurdan.ascService.dto.RepairResponseDto;
import com.yurdan.ascService.model.entity.RepairRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = EntityReferenceMapper.class)
public interface RepairRequestMapper {
    // DTO → Entity
    @Mapping(source = "deviceId", target = "device", qualifiedByName = "mapDeviceById")
    @Mapping(source = "acceptedById", target = "acceptedBy", qualifiedByName = "mapEmployeeById")
    RepairRequest toEntity(RepairRequestDto dto);
    // Entity → DTO
    @Mapping(source = "device", target = "deviceId", qualifiedByName = "mapDeviceToId")
    @Mapping(source = "acceptedBy", target = "acceptedById", qualifiedByName = "mapEmployeeToId")
    RepairResponseDto toDto(RepairRequest entity);

}

//@Mapper(componentModel = "spring")
//public interface RepairRequestMapper {
//    RepairRequest toEntity(RepairRequestDto dto);
//    RepairResponseDto toDto(RepairRequest entity);
//}