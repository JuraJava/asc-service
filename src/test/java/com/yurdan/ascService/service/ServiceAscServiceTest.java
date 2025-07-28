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
import com.yurdan.ascService.repository.ServiceCenterRepository;
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
    private ServiceCenterRepository serviceCenterRepository;

    @Mock
    private RepairRequestMapper repairRequestMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        serviceAscService = new ServiceAscService(
                repairRequestRepository,
                deviceRepository,
                employeeRepository,
                serviceCenterRepository,
                repairRequestMapper
        );
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
    void createRepairRequest_shouldReturnExistingIfSerialNumberExists() {
        // given
        RepairRequestDto dto = createDto();

        RepairRequest existingRequest = RepairRequest.builder()
                .id(42L)
                .serialNumber(dto.getSerialNumber())
                .typeOfRepair(dto.getTypeOfRepair())
                .defect(dto.getDefect())
                .appearance(dto.getAppearance())
                .customerFullName(dto.getCustomerFullName())
                .customerPatronymic(dto.getCustomerPatronymic())
                .customerAddress(dto.getCustomerAddress())
                .customerPhone(dto.getCustomerPhone())
                .build();

        RepairResponseDto expectedDto = RepairResponseDto.builder()
                .id(42L)
                .serialNumber(dto.getSerialNumber())
                .typeOfRepair(dto.getTypeOfRepair())
                .defect(dto.getDefect())
                .appearance(dto.getAppearance())
                .customerFullName(dto.getCustomerFullName())
                .customerPatronymic(dto.getCustomerPatronymic())
                .customerAddress(dto.getCustomerAddress())
                .customerPhone(dto.getCustomerPhone())
                .build();

        when(repairRequestRepository.findBySerialNumber(dto.getSerialNumber()))
                .thenReturn(Optional.of(existingRequest));
        when(repairRequestMapper.toDto(existingRequest)).thenReturn(expectedDto);

        // when
        RepairResponseDto result = serviceAscService.createRepairRequest(dto);

        // then
        verify(repairRequestRepository, never()).save(any());
        assertEquals(expectedDto.serialNumber(), result.serialNumber());
        assertEquals(expectedDto.id(), result.id());
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
        String newDefect = "Новый дефект";

        UpdateDefectDto updateDto = new UpdateDefectDto();
        updateDto.setId(requestId);
        updateDto.setDefect(newDefect);

        RepairRequest existing = new RepairRequest();
        existing.setId(requestId);
        existing.setDefect("Старый дефект");

        RepairRequest updated = new RepairRequest();
        updated.setId(requestId);
        updated.setDefect(newDefect);

        RepairResponseDto responseDto = RepairResponseDto.builder()
                .id(requestId)
                .defect(newDefect)
                .build();

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
                .defect("Не включается")
                .appearance("Нормальное")
                .customerFullName("Пупкин Иван")
                .customerPatronymic("Петрович")
                .customerAddress("Москва, Россия")
                .customerPhone("+79998885544")
                .acceptedById(2L)
                .serviceCenterId(3L)
                .build();
    }
}
