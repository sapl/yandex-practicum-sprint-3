package ru.yandex.practicum.smarthome.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.smarthome.dto.HeatingSystemDto;
import ru.yandex.practicum.smarthome.entity.HeatingSystem;

import java.util.Map;

interface ACLService {

    HeatingSystemDto getHeatingSystem(Long id);

    HeatingSystemDto updateHeatingSystem(Long id, HeatingSystemDto heatingSystemDto);

    void turnOn(Long id);

    void turnOff(Long id);

    void setTargetTemperature(Long id, double temperature);

    Double getCurrentTemperature(Long id);

    void onTelemetryReceived(Long heatingSystemId, Map<String, Object> message);

    void onStateUpdated(Long heatingSystemId, Map<String, Object> message);
}
