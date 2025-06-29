package com.yurdan.ascService.service;

import com.yurdan.ascService.dto.DeviceDto;
import com.yurdan.ascService.dto.DeviceSelectDto;
import com.yurdan.ascService.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;

    public Page<DeviceDto> getAllDevices(Pageable pageable) {
        return deviceRepository.findAllByPageable(pageable);
    }

    public List<DeviceSelectDto> getDevicesByName(String name) {
        return deviceRepository.findByPartOfName(name);
    }
}
