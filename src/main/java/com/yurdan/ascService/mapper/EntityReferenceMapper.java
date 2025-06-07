package com.yurdan.ascService.mapper;

import com.yurdan.ascService.model.entity.Device;
import com.yurdan.ascService.model.entity.Employee;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class EntityReferenceMapper {
    @Named("mapDeviceById")
    public Device mapDevice(Long id) {
        if (id == null) return null;
        Device device = new Device();
        device.setId(id);
        return device;
    }
    @Named("mapEmployeeById")
    public Employee mapEmployee(Long id) {
        if (id == null) return null;
        Employee employee = new Employee();
        employee.setId(id);
        return employee;
    }
    @Named("mapDeviceToId")
    public Long mapDevice(Device device) {
        return device != null ? device.getId() : null;
    }
    @Named("mapEmployeeToId")
    public Long mapEmployee(Employee employee) {
        return employee != null ? employee.getId() : null;
    }
}
