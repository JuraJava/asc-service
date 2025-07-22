package com.yurdan.ascService.service;

import com.yurdan.ascService.dto.RepairRequestDto;
import com.yurdan.ascService.dto.RepairResponseDto;
import com.yurdan.ascService.dto.UpdateDefectDto;
import com.yurdan.ascService.exception.DeviceNotFoundException;
import com.yurdan.ascService.exception.EmployeeNotFoundException;
import com.yurdan.ascService.mapper.RepairRequestMapper;
import com.yurdan.ascService.model.entity.Device;
import com.yurdan.ascService.model.entity.Employee;
import com.yurdan.ascService.model.entity.RepairRequest;
import com.yurdan.ascService.model.enums.RequestStatus;
import com.yurdan.ascService.model.enums.TypeOfRepair;
import com.yurdan.ascService.repository.DeviceRepository;
import com.yurdan.ascService.repository.EmployeeRepository;
import com.yurdan.ascService.repository.RepairRequestRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

/**
 * Это сервисный класс, который работает с заявками на ремонт в сервисном центре.
 * Использует Spring Data JPA Specification для построения динамических запросов.
 * Использует мапперы для преобразования сущностей в DTO.
 */
@Slf4j
@Service
public class ServiceAscService {

    private final RepairRequestRepository repairRequestRepository;
    private final DeviceRepository deviceRepository;
    private final EmployeeRepository employeeRepository;
    private final RepairRequestMapper repairRequestMapper;

    @Value("${service-center.name-of-center}")
    private String nameOfCenter;

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

    // Создание заявки на ремонт.
    public RepairResponseDto createRepairRequest(RepairRequestDto dto) {
        Optional<RepairRequest> existing = repairRequestRepository.findBySerialNumber(dto.getSerialNumber());
        if (existing.isPresent()) {
            return repairRequestMapper.toDto(existing.get());
        }

        // Проверяем наличие устройств и сотрудника
        Device device = deviceRepository.findById(dto.getDeviceId())
                .orElseThrow(() -> {
                    System.out.println("Device not found");
                    throw new DeviceNotFoundException(dto.getDeviceId());
                });
        Employee acceptedBy = employeeRepository.findById(dto.getAcceptedById())
                .orElseThrow(() -> new EmployeeNotFoundException(dto.getAcceptedById()));

        RepairRequest request = RepairRequest.builder()
                .requestStatus(RequestStatus.ACCEPTED)
                .nameOfServiceCenter(nameOfCenter)
                .phoneNumberOfServiceCenter(phoneNumber)
                .addressOfServiceCenter(address)
                .typeOfRepair(dto.getTypeOfRepair())
                .device(device)
                .serialNumber(dto.getSerialNumber())
                .saleDate(dto.getSaleDate())
                .defect(dto.getDefect())
                .appearance(dto.getAppearance())
                .customerFullName(dto.getCustomerFullName())
                .customerPatronymic(dto.getCustomerPatronymic())
                .customerAddress(dto.getCustomerAddress())
                .customerPhone(dto.getCustomerPhone())
                .acceptedBy(acceptedBy)
                .build();

        RepairRequest savedRequest =  repairRequestRepository.save(request);
        return repairRequestMapper.toDto(savedRequest);
    }

    // Получение списка заявок с фильтрацией
    public Page<RepairResponseDto> getFilteredRepairRequests(
            LocalDate createdDate,
            TypeOfRepair typeOfRepair,
            RequestStatus requestStatus,
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
        if (requestStatus != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("requestStatus"), requestStatus));
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

    // Обновление информации о дефекте в заявке
    @Transactional
    public RepairResponseDto updateDefect(UpdateDefectDto dto) {
        RepairRequest repairRequest = repairRequestRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("RepairRequest with id " + dto.getId() + " not found"));

        repairRequest.setDefect(dto.getDefect());

        RepairRequest updated = repairRequestRepository.save(repairRequest);
        return repairRequestMapper.toDto(updated);
    }
}




