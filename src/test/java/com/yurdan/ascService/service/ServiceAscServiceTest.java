package com.yurdan.ascService.service;

import com.yurdan.ascService.dto.RepairRequestDto;
import com.yurdan.ascService.dto.RepairResponseDto;
import com.yurdan.ascService.exception.DeviceNotFoundException;
import com.yurdan.ascService.mapper.RepairRequestMapper;
import com.yurdan.ascService.model.entity.Device;
import com.yurdan.ascService.model.entity.Employee;
import com.yurdan.ascService.model.entity.RepairRequest;
import com.yurdan.ascService.model.enums.TypeOfRepair;
import com.yurdan.ascService.repository.DeviceRepository;
import com.yurdan.ascService.repository.EmployeeRepository;
import com.yurdan.ascService.repository.RepairRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceAscServiceTest {

    @Mock
    private RepairRequestRepository repairRequestRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private RepairRequestMapper repairRequestMapper;

    @InjectMocks
    private ServiceAscService serviceAscService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(serviceAscService, "name", "Test ASC");
        ReflectionTestUtils.setField(serviceAscService, "phoneNumber", "123456789");
        ReflectionTestUtils.setField(serviceAscService, "address", "Test Street");
    }

    @Test
    void createRepairRequest_whenValidRequest_savesAndReturnsDto() {
        // given
        RepairRequestDto dto = new RepairRequestDto();
        dto.setDeviceId(1L);
        dto.setTypeOfRepair(TypeOfRepair.NON_WARRANTY);
        dto.setSerialNumber("SN123456");
        dto.setSaleDate(LocalDate.of(2023, 12, 1));
        dto.setDefect("Does not boot");
        dto.setAppearance("Slight scratches");
        dto.setCost(new BigDecimal("150.00"));
        dto.setCustomerFullName("John Doe");
        dto.setCustomerPatronymic("Eduardovich");
        dto.setCustomerAddress("123 Test Street");
        dto.setCustomerPhone("+1234567890");
        dto.setAcceptedById(5L);

        when(repairRequestRepository.findBySerialNumber("SN123456"))
                .thenReturn(Optional.empty());

        Device mockDevice = new Device();
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(mockDevice));

        Employee mockEmployee = new Employee();
        when(employeeRepository.findById(5L)).thenReturn(Optional.of(mockEmployee));

        RepairRequest mockEntity = new RepairRequest();
        when(repairRequestMapper.toEntity(dto)).thenReturn(mockEntity);

        RepairRequest savedEntity = new RepairRequest();
        savedEntity.setId(100L);
        when(repairRequestRepository.save(mockEntity)).thenReturn(savedEntity);

        RepairResponseDto expectedResponse = new RepairResponseDto();
        expectedResponse.setId(100L);
        expectedResponse.setCustomerFullName("John Doe");
        expectedResponse.setSerialNumber("SN123456");

        when(repairRequestMapper.toDto(savedEntity)).thenReturn(expectedResponse);

        // when
        RepairResponseDto result = serviceAscService.createRepairRequest(dto);

        // then
        assertNotNull(result);
        assertEquals("John Doe", result.getCustomerFullName());
        assertEquals("SN123456", result.getSerialNumber());
        assertEquals(100L, result.getId());

        verify(repairRequestRepository).save(mockEntity);
        verify(repairRequestMapper).toDto(savedEntity);
    }

    @Test
    void createRepairRequest_whenRequestExists_returnsExisting() {
        RepairRequestDto dto = new RepairRequestDto();
        dto.setSerialNumber("ABC123");

        RepairRequest existing = new RepairRequest();
        existing.setSerialNumber("ABC123");

        when(repairRequestRepository.findBySerialNumber("ABC123"))
                .thenReturn(Optional.of(existing));
        when(repairRequestMapper.toDto(existing))
                .thenReturn(new RepairResponseDto());

        RepairResponseDto result = serviceAscService.createRepairRequest(dto);

        assertNotNull(result);
        verify(repairRequestRepository, never()).save(any());
    }

    @Test
    void createRepairRequest_deviceNotFound_throwsException() {
        RepairRequestDto dto = new RepairRequestDto();
        dto.setSerialNumber("NEW123");
        dto.setDeviceId(99L);
        dto.setAcceptedById(2L);

        when(repairRequestRepository.findBySerialNumber("NEW123"))
                .thenReturn(Optional.empty());
        when(deviceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(DeviceNotFoundException.class, () -> {
            serviceAscService.createRepairRequest(dto);
        });
    }

    @Test
    void getFilteredRepairRequests_filtersApplied_returnsPage() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        TypeOfRepair repairType = TypeOfRepair.NON_WARRANTY;
        Long deviceId = 1L;
        String name = "John";
        Long acceptedById = 2L;

        Pageable pageable = PageRequest.of(0, 10);
        List<RepairRequest> requests = List.of(new RepairRequest());
        Page<RepairRequest> page = new PageImpl<>(requests);

        when(repairRequestRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(repairRequestMapper.toDto(any())).thenReturn(new RepairResponseDto());

        Page<RepairResponseDto> result = serviceAscService.getFilteredRepairRequests(
                date, repairType, deviceId, name, acceptedById, pageable
        );

        assertEquals(1, result.getContent().size());
    }
}
