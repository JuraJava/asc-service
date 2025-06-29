//package com.yurdan.ascService.service;
//
//import com.yurdan.ascService.dto.RepairRequestDto;
//import com.yurdan.ascService.dto.RepairResponseDto;
//import com.yurdan.ascService.dto.UpdateDefectDto;
//import com.yurdan.ascService.exception.DeviceNotFoundException;
//import com.yurdan.ascService.exception.EmployeeNotFoundException;
//import com.yurdan.ascService.mapper.RepairRequestMapper;
//import com.yurdan.ascService.model.entity.Device;
//import com.yurdan.ascService.model.entity.Employee;
//import com.yurdan.ascService.model.entity.RepairRequest;
//import com.yurdan.ascService.model.enums.TypeOfRepair;
//import com.yurdan.ascService.repository.DeviceRepository;
//import com.yurdan.ascService.repository.EmployeeRepository;
//import com.yurdan.ascService.repository.RepairRequestRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.data.domain.*;
//import org.springframework.data.jpa.domain.Specification;
//
//import java.time.LocalDate;
//import java.util.*;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class ServiceAscServiceTest {
//
//    @Mock
//    private RepairRequestRepository repairRequestRepository;
//    @Mock
//    private DeviceRepository deviceRepository;
//    @Mock
//    private EmployeeRepository employeeRepository;
//    @Mock
//    private RepairRequestMapper repairRequestMapper;
//
//    @InjectMocks
//    private ServiceAscService service;
//
//    private final String nameOfCenter = "Test Center";
//    private final String phoneNumber = "+123456789";
//    private final String address = "123 Main St";
//
//    @BeforeEach
//    void init() {
//        MockitoAnnotations.openMocks(this);
//        // Вручную установить значения полей, аннотированных @Value
//        service = new ServiceAscService(repairRequestRepository, deviceRepository, employeeRepository, repairRequestMapper);
//        // Установка через reflection
//        setPrivateField("nameOfCenter", nameOfCenter);
//        setPrivateField("phoneNumber", phoneNumber);
//        setPrivateField("address", address);
//    }
//
//    private void setPrivateField(String fieldName, String value) {
//        try {
//            var field = ServiceAscService.class.getDeclaredField(fieldName);
//            field.setAccessible(true);
//            field.set(service, value);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Test
//    void createRepairRequest_existingSerialNumber_returnsExistingSaved() {
//        RepairRequestDto dto = buildValidDto();
//        RepairRequest existing = new RepairRequest();
//        when(repairRequestRepository.findBySerialNumber(dto.getSerialNumber())).thenReturn(Optional.of(existing));
//        when(repairRequestRepository.save(existing)).thenReturn(existing);
//
//        RepairRequest result = service.createRepairRequest(dto);
//
//        assertThat(result).isSameAs(existing);
//        verify(deviceRepository, never()).findById(any());
//    }
//
//    @Test
//    void createRepairRequest_deviceNotFound_throwsException() {
//        RepairRequestDto dto = buildValidDto();
//        when(repairRequestRepository.findBySerialNumber(dto.getSerialNumber())).thenReturn(Optional.empty());
//        when(deviceRepository.findById(dto.getDeviceId())).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> service.createRepairRequest(dto))
//                .isInstanceOf(DeviceNotFoundException.class);
//    }
//
//    @Test
//    void createRepairRequest_employeeNotFound_throwsException() {
//        RepairRequestDto dto = buildValidDto();
//        Device device = new Device();
//        when(repairRequestRepository.findBySerialNumber(dto.getSerialNumber())).thenReturn(Optional.empty());
//        when(deviceRepository.findById(dto.getDeviceId())).thenReturn(Optional.of(device));
//        when(employeeRepository.findById(dto.getAcceptedById())).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> service.createRepairRequest(dto))
//                .isInstanceOf(EmployeeNotFoundException.class);
//    }
//
//    @Test
//    void createRepairRequest_success() {
//        RepairRequestDto dto = buildValidDto();
//        Device device = new Device();
//        Employee employee = new Employee();
//        RepairRequest savedRequest = new RepairRequest();
//
//        when(repairRequestRepository.findBySerialNumber(dto.getSerialNumber())).thenReturn(Optional.empty());
//        when(deviceRepository.findById(dto.getDeviceId())).thenReturn(Optional.of(device));
//        when(employeeRepository.findById(dto.getAcceptedById())).thenReturn(Optional.of(employee));
//        when(repairRequestRepository.save(any())).thenReturn(savedRequest);
//
//        RepairRequest result = service.createRepairRequest(dto);
//
//        assertThat(result).isEqualTo(savedRequest);
//    }
//
//    @Test
//    void getFilteredRepairRequests_allFiltersNull_returnsAll() {
//        Pageable pageable = PageRequest.of(0, 10);
//        RepairRequest entity = new RepairRequest();
//        RepairResponseDto responseDto = new RepairResponseDto();
//
//        when(repairRequestRepository.findAll(any(Specification.class), eq(pageable)))
//                .thenReturn(new PageImpl<>(List.of(entity)));
//        when(repairRequestMapper.toDto(entity)).thenReturn(responseDto);
//
//        Page<RepairResponseDto> page = service.getFilteredRepairRequests(
//                null, null, null, null, null, null, pageable
//        );
//
//        assertThat(page.getContent()).containsExactly(responseDto);
//    }
//
//    @Test
//    void updateDefect_successfulUpdate() {
//        UpdateDefectDto dto = new UpdateDefectDto();
//        dto.setId(1L);
//        dto.setDefect("New defect");
//
//        RepairRequest request = new RepairRequest();
//        RepairRequest updated = new RepairRequest();
//        RepairResponseDto responseDto = new RepairResponseDto();
//
//        when(repairRequestRepository.findById(1L)).thenReturn(Optional.of(request));
//        when(repairRequestRepository.save(request)).thenReturn(updated);
//        when(repairRequestMapper.toDto(updated)).thenReturn(responseDto);
//
//        RepairResponseDto result = service.updateDefect(dto);
//
//        assertThat(result).isSameAs(responseDto);
//        assertThat(request.getDefect()).isEqualTo("New defect");
//    }
//
//    @Test
//    void updateDefect_notFound_throwsException() {
//        UpdateDefectDto dto = new UpdateDefectDto();
//        dto.setId(99L);
//        when(repairRequestRepository.findById(99L)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> service.updateDefect(dto))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("RepairRequest with id 99 not found");
//    }
//
//    // --- Вспомогательные методы ---
//
//    private RepairRequestDto buildValidDto() {
//        return RepairRequestDto.builder()
//                .deviceId(1L)
//                .acceptedById(2L)
//                .typeOfRepair(TypeOfRepair.WARRANTY)
//                .serialNumber("SN123")
//                .saleDate(LocalDate.now())
//                .defect("Doesn't turn on")
//                .appearance("Clean")
//                .customerFullName("Test User")
//                .customerPatronymic("Ivanovich")
//                .customerAddress("123 Main St")
//                .customerPhone("+123456789")
//                .build();
//    }
//}
