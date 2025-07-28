package com.yurdan.ascService.mapper;

import com.yurdan.ascService.dto.CreateWorkOrderDto;
import com.yurdan.ascService.dto.WorkOrderResponseDto;
import com.yurdan.ascService.model.entity.WorkOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Преобразует объект CreateWorkOrderDto → в сущность WorkOrder чтобы сохранить в БД,
 * преобразует сущность WorkOrder → в WorkOrderResponseDto для отправки клиенту через API,
 * использует вспомогательные методы из EntityReferenceMapper для обработки связанных сущностей.
 */
@Mapper(componentModel = "spring", uses = {
        EntityReferenceMapper.class,
        EmployeeMapper.class,
        CompletedWorkMapper.class,
        SparePartMapper.class
})
 public interface WorkOrderMapper {
    // DTO → Entity
    @Mapping(source = "repairRequestId", target = "repairRequest", qualifiedByName = "mapRepairRequestById")
    @Mapping(source = "completedWorkIds", target = "completedWorks", qualifiedByName = "mapCompletedWorksById")
    @Mapping(source = "sparePartIds", target = "spareParts", qualifiedByName = "mapSparePartsById")
//    @Mapping(source = "performedById", target = "performedBy", qualifiedByName = "mapEmployeeById")
    WorkOrder toEntity(CreateWorkOrderDto dto);
    // Entity → DTO
    @Mapping(source = "repairRequest", target = "repairRequestId", qualifiedByName = "mapRepairRequestToId")
    @Mapping(source = "performedBy", target = "performedBy")
    @Mapping(source = "completedWorks", target = "completedWorks")
    @Mapping(source = "spareParts", target = "spareParts")
    WorkOrderResponseDto toDto(WorkOrder entity);
}
