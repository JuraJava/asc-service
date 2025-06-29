package com.yurdan.ascService.controller.rest;

import com.yurdan.ascService.dto.RepairRequestDto;
import com.yurdan.ascService.dto.RepairResponseDto;
import com.yurdan.ascService.model.enums.TypeOfRepair;
import com.yurdan.ascService.service.ServiceAscService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/asc/repair-request")
public class RepairRequestController {

    private final ServiceAscService serviceAscService;

    public RepairRequestController(ServiceAscService serviceAscService) {
        this.serviceAscService = serviceAscService;
    }

    @PostMapping("/create-repair-request")
    public ResponseEntity<RepairResponseDto> createRepairRequest(@Valid @RequestBody RepairRequestDto dto) {
        log.info("Создание новой заявки: {}", dto);
        RepairResponseDto response = serviceAscService.createRepairRequest(dto);
        log.info("Заявка создана: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/repair-requests")
    public ResponseEntity<Page<RepairResponseDto>> getRepairRequests(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdDate,
            @RequestParam(required = false) TypeOfRepair typeOfRepair,
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) String customerFullName,
            @RequestParam(required = false) Long acceptedById,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "id,desc") String sort
    ) {
        log.info("Получение списка заявок на ремонт");

        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 ?
                Sort.Direction.fromString(sortParams[1]) :
                Sort.Direction.DESC;

        PageRequest pageRequest = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 10,
                Sort.by(direction, sortParams[0])
        );

        Page<RepairResponseDto> response = serviceAscService.getFilteredRepairRequests(
                createdDate, typeOfRepair, deviceId, customerFullName, acceptedById, pageRequest
        );
        return ResponseEntity.ok(response);
    }
}

