package com.yurdan.ascService.service;

import com.yurdan.ascService.dto.RepairRequestDto;
import com.yurdan.ascService.dto.RepairResponseDto;
import com.yurdan.ascService.exception.DeviceNotFoundException;
import com.yurdan.ascService.exception.EmployeeNotFoundException;
import com.yurdan.ascService.mapper.RepairRequestMapper;
import com.yurdan.ascService.model.entity.RepairRequest;
import com.yurdan.ascService.model.enums.TypeOfRepair;
import com.yurdan.ascService.repository.DeviceRepository;
import com.yurdan.ascService.repository.EmployeeRepository;
import com.yurdan.ascService.repository.RepairRequestRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class ServiceAscService {

    private final RepairRequestRepository repairRequestRepository;
    private final DeviceRepository deviceRepository;
    private final EmployeeRepository employeeRepository;
    private final RepairRequestMapper repairRequestMapper;

    @Value("${service-center.name}")
    private String name;

    @Value("${service-center.phone-number}")
    private String phoneNumber;

    @Value("${service-center.address}")
    private String address;

    public ServiceAscService(RepairRequestRepository repairRequestRepository,
                             DeviceRepository deviceRepository,
                             EmployeeRepository employeeRepository,
                             RepairRequestMapper repairRequestMapper) {
        this.repairRequestRepository = repairRequestRepository;
        this.deviceRepository = deviceRepository;
        this.employeeRepository = employeeRepository;
        this.repairRequestMapper = repairRequestMapper;
    }

    public RepairResponseDto createRepairRequest(RepairRequestDto dto) {
        Optional<RepairRequest> existing = repairRequestRepository.findBySerialNumber(dto.getSerialNumber());
        if (existing.isPresent()) {
            return repairRequestMapper.toDto(existing.get());
        }

        // Проверяем наличие устройств и сотрудника
        deviceRepository.findById(dto.getDeviceId())
                .orElseThrow(() -> new DeviceNotFoundException(dto.getDeviceId()));
        employeeRepository.findById(dto.getAcceptedById())
                .orElseThrow(() -> new EmployeeNotFoundException(dto.getAcceptedById()));

        RepairRequest request = repairRequestMapper.toEntity(dto);
        request.setName(name);
        request.setPhoneNumber(phoneNumber);
        request.setAddress(address);

        RepairRequest saved = repairRequestRepository.save(request);
        return repairRequestMapper.toDto(saved);
    }

    public Page<RepairResponseDto> getFilteredRepairRequests(
            LocalDate createdDate,
            TypeOfRepair typeOfRepair,
            Long deviceId,
            String customerFullName,
            Long acceptedById,
            Pageable pageable
    ) {
        Specification<RepairRequest> spec = Specification.where(null);

        if (createdDate != null) {
            LocalDateTime startOfDay = createdDate.atStartOfDay();
            LocalDateTime endOfDay = createdDate.atTime(LocalTime.MAX);
            spec = spec.and((root, query, cb) ->
                    cb.between(root.get("createdAt"), startOfDay, endOfDay));
        }
        if (typeOfRepair != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("typeOfRepair"), typeOfRepair));
        }
        if (deviceId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("device").get("id"), deviceId));
        }
        if (customerFullName != null) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("customerFullName")), "%" + customerFullName.toLowerCase() + "%"));
        }
        if (acceptedById != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("acceptedBy").get("id"), acceptedById));
        }

        return repairRequestRepository.findAll(spec, pageable)
                .map(repairRequestMapper::toDto);
    }
}




