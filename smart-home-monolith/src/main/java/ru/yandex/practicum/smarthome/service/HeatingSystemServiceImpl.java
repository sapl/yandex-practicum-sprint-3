package ru.yandex.practicum.smarthome.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.smarthome.dto.HeatingSystemDto;
import ru.yandex.practicum.smarthome.entity.HeatingSystem;
import ru.yandex.practicum.smarthome.repository.HeatingSystemRepository;

@Service
@RequiredArgsConstructor
public class HeatingSystemServiceImpl implements HeatingSystemService {
    private final ACLService aclService;

    @Override
    public HeatingSystemDto getHeatingSystem(Long id) {
        return aclService.getHeatingSystem(id);
    }

    @Override
    public Double getCurrentTemperature(Long id) {
        return aclService.getCurrentTemperature(id);
    }

    @Override
    public HeatingSystemDto updateHeatingSystem(Long id, HeatingSystemDto heatingSystemDto) {
        return aclService.updateHeatingSystem(id, heatingSystemDto);
    }

    @Override
    public void turnOn(Long id) {
        aclService.turnOn(id);
    }

    @Override
    public void turnOff(Long id) {
        aclService.turnOff(id);
    }

    @Override
    public void setTargetTemperature(Long id, double temperature) {
        aclService.setTargetTemperature(id, temperature);
    }


}