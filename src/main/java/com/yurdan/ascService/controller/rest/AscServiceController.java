package com.yurdan.ascService.controller.rest;

import com.yurdan.ascService.dto.RepairRequestDto;
import com.yurdan.ascService.dto.RepairResponseDto;
import com.yurdan.ascService.model.enums.TypeOfRepair;
import com.yurdan.ascService.service.ServiceAscService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/asc")
public class AscServiceController {

    private final ServiceAscService serviceAscService;

    public AscServiceController(ServiceAscService serviceAscService) {
        this.serviceAscService = serviceAscService;
    }

    @PostMapping("/create-repair-request")
    public ResponseEntity<RepairResponseDto> createRepairRequest(@Valid @RequestBody RepairRequestDto dto) {
        RepairResponseDto response = serviceAscService.createRepairRequest(dto);
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
            @RequestParam(required = false) Integer size
    ) {
        PageRequest pageRequest = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 10
        );

        Page<RepairResponseDto> response = serviceAscService.getFilteredRepairRequests(
                createdDate, typeOfRepair, deviceId, customerFullName, acceptedById, pageRequest
        );
        return ResponseEntity.ok(response);
    }
}

