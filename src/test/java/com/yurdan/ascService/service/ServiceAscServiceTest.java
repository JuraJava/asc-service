package com.yurdan.ascService.service;

import com.yurdan.ascService.dto.RepairRequestDto;
import com.yurdan.ascService.dto.RepairResponseDto;
import com.yurdan.ascService.dto.UpdateDefectDto;
import com.yurdan.ascService.exception.DeviceNotFoundException;
import com.yurdan.ascService.exception.EmployeeNotFoundException;
import com.yurdan.ascService.mapper.RepairRequestMapper;
import com.yurdan.ascService.model.entity.*;
import com.yurdan.ascService.model.enums.TypeOfRepair;
import com.yurdan.ascService.repository.DeviceRepository;
import com.yurdan.ascService.repository.EmployeeRepository;
import com.yurdan.ascService.repository.RepairRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceAscServiceTest {

    @InjectMocks
    private ServiceAscService serviceAscService;

    @Mock
    private RepairRequestRepository repairRequestRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private RepairRequestMapper repairRequestMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Установка значений полей @Value
        serviceAscService = new ServiceAscService(
                repairRequestRepository, deviceRepository, employeeRepository, repairRequestMapper
        );
        setField(serviceAscService, "nameOfCenter", "ASC");
        setField(serviceAscService, "phoneNumber", "+380991112233");
        setField(serviceAscService, "address", "Kyiv, ASC Street");
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createRepairRequest_shouldReturnExistingRequestIfSerialExists() {
        // Arrange
        RepairRequestDto dto = createDto();
        RepairRequest existing = new RepairRequest();
        when(repairRequestRepository.findBySerialNumber(dto.getSerialNumber()))
                .thenReturn(Optional.of(existing));
        when(repairRequestRepository.save(existing)).thenReturn(existing);

        // Act
        RepairRequest result = serviceAscService.createRepairRequest(dto);

        // Assert
        assertEquals(existing, result);
        verify(repairRequestRepository, times(1)).save(existing);
    }

    @Test
    void createRepairRequest_shouldThrowIfDeviceNotFound() {
        RepairRequestDto dto = createDto();

        when(repairRequestRepository.findBySerialNumber(dto.getSerialNumber()))
                .thenReturn(Optional.empty());
        when(deviceRepository.findById(dto.getDeviceId())).thenReturn(Optional.empty());

        assertThrows(DeviceNotFoundException.class, () -> {
            serviceAscService.createRepairRequest(dto);
        });
    }

    @Test
    void createRepairRequest_shouldThrowIfEmployeeNotFound() {
        RepairRequestDto dto = createDto();
        Device device = new Device();
        device.setId(1L);

        when(repairRequestRepository.findBySerialNumber(dto.getSerialNumber()))
                .thenReturn(Optional.empty());
        when(deviceRepository.findById(dto.getDeviceId())).thenReturn(Optional.of(device));
        when(employeeRepository.findById(dto.getAcceptedById())).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> {
            serviceAscService.createRepairRequest(dto);
        });
    }

    @Test
    void createRepairRequest_shouldCreateNewRequestIfValid() {
        RepairRequestDto dto = createDto();
        Device device = new Device();
        device.setId(1L);
        Employee employee = new Employee();
        employee.setId(2L);

        RepairRequest saved = new RepairRequest();
        saved.setId(100L);

        when(repairRequestRepository.findBySerialNumber(dto.getSerialNumber()))
                .thenReturn(Optional.empty());
        when(deviceRepository.findById(dto.getDeviceId())).thenReturn(Optional.of(device));
        when(employeeRepository.findById(dto.getAcceptedById())).thenReturn(Optional.of(employee));
        when(repairRequestRepository.save(any(RepairRequest.class))).thenReturn(saved);

        RepairRequest result = serviceAscService.createRepairRequest(dto);
        assertEquals(saved.getId(), result.getId());
    }

    @Test
    void getFilteredRepairRequests_shouldReturnPageOfDtos() {
        RepairRequest entity = new RepairRequest();
        entity.setId(1L);
        RepairResponseDto dto = RepairResponseDto.builder().id(1L).build();

        Pageable pageable = PageRequest.of(0, 10);
        when(repairRequestRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(entity)));
        when(repairRequestMapper.toDto(entity)).thenReturn(dto);

        Page<RepairResponseDto> result = serviceAscService.getFilteredRepairRequests(
                null, null, null, null, null, null, pageable
        );

        assertEquals(1, result.getContent().size());
        assertEquals(dto.id(), result.getContent().get(0).id());
    }

    @Test
    void updateDefect_shouldUpdateAndReturnDto() {
        Long requestId = 1L;
        String newDefect = "New defect";

        UpdateDefectDto updateDto = new UpdateDefectDto();
        updateDto.setId(requestId);
        updateDto.setDefect(newDefect);

        RepairRequest existing = new RepairRequest();
        existing.setId(requestId);
        existing.setDefect("Old defect");

        RepairRequest updated = new RepairRequest();
        updated.setId(requestId);
        updated.setDefect(newDefect);

        RepairResponseDto responseDto = RepairResponseDto.builder().id(requestId).defect(newDefect).build();

        when(repairRequestRepository.findById(requestId)).thenReturn(Optional.of(existing));
        when(repairRequestRepository.save(existing)).thenReturn(updated);
        when(repairRequestMapper.toDto(updated)).thenReturn(responseDto);

        RepairResponseDto result = serviceAscService.updateDefect(updateDto);

        assertEquals(newDefect, result.defect());
    }

    @Test
    void updateDefect_shouldThrowIfRequestNotFound() {
        UpdateDefectDto dto = new UpdateDefectDto();
        dto.setId(999L);
        dto.setDefect("Updated defect");

        when(repairRequestRepository.findById(dto.getId())).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            serviceAscService.updateDefect(dto);
        });

        assertTrue(ex.getMessage().contains("RepairRequest with id"));
    }

    private RepairRequestDto createDto() {
        return RepairRequestDto.builder()
                .deviceId(1L)
                .typeOfRepair(TypeOfRepair.WARRANTY)
                .serialNumber("ABC123")
                .saleDate(LocalDate.now().minusDays(30))
                .defect("Not working")
                .appearance("Good")
                .customerFullName("John Doe")
                .customerPatronymic("Middle")
                .customerAddress("Kyiv, Ukraine")
                .customerPhone("+380990001122")
                .acceptedById(2L)
                .build();
    }
}
