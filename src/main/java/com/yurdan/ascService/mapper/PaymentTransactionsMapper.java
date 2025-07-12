package com.yurdan.ascService.mapper;

import com.yurdan.ascService.dto.PaymentTransactionsRequestDto;
import com.yurdan.ascService.dto.PaymentTransactionsResponseDto;
import com.yurdan.ascService.model.entity.Employee;
import com.yurdan.ascService.model.entity.PaymentTransactions;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = EntityReferenceMapper.class)
public interface PaymentTransactionsMapper {
    // DTO → Entity
    @Mapping(source = "repairRequestId", target = "repairRequest", qualifiedByName = "mapRepairRequestById")
    @Mapping(source = "acceptedById", target = "acceptedBy", qualifiedByName = "mapEmployeeById")
    PaymentTransactions toEntity(PaymentTransactionsRequestDto dto);

    // Entity → DTO
    @Mapping(source = "repairRequest", target = "repairRequestId", qualifiedByName = "mapRepairRequestToId")
    @Mapping(source = "acceptedBy", target = "acceptedBy", qualifiedByName = "mapEmployeeToId")
//    @Mapping(source = "acceptedBy", target = "acceptedBy", qualifiedByName = "formatEmployeeName")
    PaymentTransactionsResponseDto toDto(PaymentTransactions entity);

//    @Named("formatEmployeeName")
//    default String formatEmployeeName(Employee acceptedBy) {
//        if (acceptedBy == null) {
//            return null;
//        }
//        return acceptedBy.getFullName();
//    }

}

