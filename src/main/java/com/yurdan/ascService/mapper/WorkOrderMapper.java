package com.yurdan.ascService.mapper;

import com.yurdan.ascService.dto.CreateWorkOrderDto;
import com.yurdan.ascService.dto.WorkOrderResponseDto;
import com.yurdan.ascService.model.entity.WorkOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

 @Mapper(componentModel = "spring", uses = EntityReferenceMapper.class)
 public interface WorkOrderMapper {
    // DTO → Entity
    @Mapping(source = "repairRequestId", target = "repairRequest", qualifiedByName = "mapRepairRequestById")
    @Mapping(source = "completedWorkIds", target = "completedWorks", qualifiedByName = "mapCompletedWorksById")
    @Mapping(source = "sparePartIds", target = "spareParts", qualifiedByName = "mapSparePartsById")
//    @Mapping(source = "performedById", target = "performedBy", qualifiedByName = "mapEmployeeById")
    WorkOrder toEntity(CreateWorkOrderDto dto);
    // Entity → DTO
    @Mapping(source = "repairRequest", target = "repairRequestId", qualifiedByName = "mapRepairRequestToId")
    @Mapping(source = "completedWorks", target = "completedWorkIds", qualifiedByName = "mapCompletedWorksToId")
    @Mapping(source = "spareParts", target = "sparePartIds", qualifiedByName = "mapSparePartsToId")
    @Mapping(source = "performedBy", target = "performedById", qualifiedByName = "mapEmployeeToId")
    WorkOrderResponseDto toDto(WorkOrder entity);
}
