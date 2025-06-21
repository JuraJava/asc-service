package com.yurdan.ascService.mapper;

import com.yurdan.ascService.model.entity.*;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class EntityReferenceMapper {

    @Named("mapDeviceById")
    public Device mapDevice(Long id) {
        if (id == null) return null;
        Device device = new Device();
        device.setId(id);
        return device;
    }

    @Named("mapDeviceToId")
    public Long mapDevice(Device device) {
        return device != null ? device.getId() : null;
    }

    @Named("mapEmployeeById")
    public Employee mapEmployee(Long id) {
        if (id == null) return null;
        Employee employee = new Employee();
        employee.setId(id);
        return employee;
    }

    @Named("mapEmployeeToId")
    public Long mapEmployee(Employee employee) {
        return employee != null ? employee.getId() : null;
    }

    @Named("mapRepairRequestById")
    public RepairRequest mapRepairRequestById(Long id) {
        if (id == null) return null;
        RepairRequest repairRequest = new RepairRequest();
        repairRequest.setId(id);
        return repairRequest;
    }

    @Named("mapRepairRequestToId")
    public Long mapRepairRequestToId(RepairRequest repairRequest) {
        return repairRequest != null ? repairRequest.getId() : null;
    }

    @Named("mapCompletedWorksById")
    public List<CompletedWork> mapCompletedWorksById (List<Long> completedWorkIds) {
        if (completedWorkIds == null || completedWorkIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<CompletedWork> completedWorks = new ArrayList<>();
        for (Long id : completedWorkIds) {
            if (id != null) {
                CompletedWork completedWork = new CompletedWork();
                completedWork.setId(id);
                completedWorks.add(completedWork);
            }
        }

        return completedWorks;
    }

    @Named("mapCompletedWorksToId")
    public List<Long> mapCompletedWorksToId(List<CompletedWork> completedWorks) {
        if (completedWorks == null || completedWorks.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> completedWorkIds = new ArrayList<>();
        for (CompletedWork work : completedWorks) {
            if (work != null) {
                completedWorkIds.add(work.getId());
            }
        }
        return completedWorkIds;
    }

    @Named("mapSparePartsById")
    public List<SparePart> mapSparePartsById(List<Long> sparePartIds) {
        if (sparePartIds == null || sparePartIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<SparePart> spareParts = new ArrayList<>();
        for (Long id : sparePartIds) {
            if (id != null) {
                SparePart sparePart = new SparePart();
                sparePart.setId(id);
                spareParts.add(sparePart);
            }
        }

        return spareParts;
    }

    @Named("mapSparePartsToId")
    public List<Long> mapSparePartsToId(List<SparePart> spareParts) {
        if (spareParts == null || spareParts.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> sparePartIds = new ArrayList<>();
        for (SparePart part : spareParts) {
            if (part != null) {
                sparePartIds.add(part.getId());
            }
        }

        return sparePartIds;
    }

}
