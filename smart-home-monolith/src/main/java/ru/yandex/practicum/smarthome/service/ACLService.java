package ru.yandex.practicum.smarthome.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.smarthome.dto.HeatingSystemDto;

interface ACLService {

    HeatingSystemDto getHeatingSystem(Long id);
    HeatingSystemDto updateHeatingSystem(Long id, HeatingSystemDto heatingSystemDto);
    void turnOn(Long id);
    void turnOff(Long id);
    void setTargetTemperature(Long id, double temperature);
    Double getCurrentTemperature(Long id);
}
