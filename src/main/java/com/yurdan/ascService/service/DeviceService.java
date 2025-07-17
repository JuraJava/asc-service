package com.yurdan.ascService.service;

import com.yurdan.ascService.dto.DeviceDto;
import com.yurdan.ascService.dto.DeviceSelectDto;
import com.yurdan.ascService.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Этот класс находится в сервисном слое, т.е. слое бизнес-логики — посредник между
 * контроллерами (веб-слоем) и репозиториями (доступ к БД).
 */
@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;

    // Получение всех устройств постранично
    public Page<DeviceDto> getAllDevices(Pageable pageable) {
        return deviceRepository.findAllByPageable(pageable);
    }

    // Поиск устройств по части имени
    public List<DeviceSelectDto> getDevicesByName(String name) {
        return deviceRepository.findByPartOfName(name);
    }
}
