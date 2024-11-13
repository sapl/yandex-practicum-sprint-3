package ru.yandex.practicum.smarthome.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HeatingSystemDto {
    private Long id;
    private Boolean isOn;
    private Double targetTemperature;
    private Double currentTemperature;
}