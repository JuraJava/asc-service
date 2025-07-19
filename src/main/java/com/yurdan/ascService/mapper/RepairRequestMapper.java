package com.yurdan.ascService.mapper;

import com.yurdan.ascService.dto.RepairRequestDto;
import com.yurdan.ascService.dto.RepairResponseDto;
import com.yurdan.ascService.model.entity.Device;
import com.yurdan.ascService.model.entity.Employee;
import com.yurdan.ascService.model.entity.RepairRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Этот интерфейс отвечает за маппинг между DTO и Entity:
 * преобразует RepairRequestDto → RepairRequest (DTO → Entity) и обратно,
 * подключает вспомогательный EntityReferenceMapper для преобразования ID ↔ Entity
 */
@Mapper(componentModel = "spring", uses = EntityReferenceMapper.class)
public interface RepairRequestMapper {

    // DTO → Entity
    @Mapping(source = "deviceId", target = "device", qualifiedByName = "mapDeviceById")
    @Mapping(source = "acceptedById", target = "acceptedBy", qualifiedByName = "mapEmployeeById")
    RepairRequest toEntity(RepairRequestDto dto);
    // Entity → DTO
    @Mapping(source = "device", target = "device", qualifiedByName = "formatDeviceName")
    @Mapping(source = "acceptedBy", target = "acceptedBy", qualifiedByName = "formatEmployeeName")
    RepairResponseDto toDto(RepairRequest entity);

    @Named("formatDeviceName")
    default String formatDeviceName(Device device) {
        if (device == null) {
            return null;
        }
        return device.getDeviceName() + " " + device.getDeviceColor().name();
    }

    @Named("formatEmployeeName")
    default String formatEmployeeName(Employee acceptedBy) {
        if (acceptedBy == null) {
            return null;
        }
        return acceptedBy.getFullName();
    }
}
