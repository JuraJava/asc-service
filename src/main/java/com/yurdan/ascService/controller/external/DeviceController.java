package com.yurdan.ascService.controller.external;

import com.yurdan.ascService.dto.DeviceDto;
import com.yurdan.ascService.dto.DeviceSelectDto;
import com.yurdan.ascService.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Этот класс  — это REST-контроллер, который обрабатывает HTTP-запросы, связанные с устройствами (Device).
 * Он работает с DTO-объектами (DeviceDto, DeviceSelectDto) и взаимодействует с сервисным слоем через DeviceService.
 * Предоставляет два API:
 * /asc/device/get-all     -   для получения всех устройств с пагинацией и сортировкой,
 * и
 * /asc/device/get-devices-for-select?name=...
 * для выбора устройства из списка по части названия.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/asc/device")
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping("get-all")
    public Page<DeviceDto> getAllDevices(@RequestParam(required = false) Integer page,
                                         @RequestParam(required = false) Integer size,
                                         @RequestParam(defaultValue = "deviceName,asc", required = false) String sort) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 ?
                Sort.Direction.fromString(sortParams[1]) :
                Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 25,
                Sort.by(direction, sortParams[0])
        );
        return deviceService.getAllDevices(pageable);
    }

    @GetMapping("/get-devices-for-select")
    public ResponseEntity<List<DeviceSelectDto>> getDevicesForSelect(@RequestParam String name) {
        log.info("Запрос на получение устройств для выбора: {}", name);
        List<DeviceSelectDto> devices = deviceService.getDevicesByName(name);
        return ResponseEntity.ok(devices);
    }
}

