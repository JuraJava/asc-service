package com.yurdan.ascService.mapper;

import com.yurdan.ascService.dto.EmployeeDto;
import com.yurdan.ascService.model.entity.Employee;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    EmployeeDto toDto(Employee employee);
}
