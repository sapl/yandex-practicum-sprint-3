package ru.yandex.practicum.smarthome.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.smarthome.dto.HeatingSystemDto;
import ru.yandex.practicum.smarthome.entity.HeatingSystem;
import ru.yandex.practicum.smarthome.repository.HeatingSystemRepository;

import java.util.HashMap;
import java.util.Map;

@Service
public class ACLServiceImpl implements ACLService {

    private final RestTemplate restTemplate;
    private final String deviceCoreServiceUrl;
    private final HeatingSystemRepository heatingSystemRepository;

    // TODO если бы был реальный сервис
    //  - корректный проброс и обработка ошибок
    //  - кеширование
    //  - отдельный интерфейс для маппинга статуса и телеметрии
    //  - какая логика по регистрации новых устройств в Iot хабе
    //  - мы предполагаем что данные телеметрии уже мигрированы в новый сервис телеметрии

    public ACLServiceImpl(
            HeatingSystemRepository heatingSystemRepository,
            RestTemplate restTemplate,
            @Value("${services.sh-device-core.url}") String deviceCoreServiceUrl) {
        this.restTemplate = restTemplate;
        this.heatingSystemRepository = heatingSystemRepository;
        this.deviceCoreServiceUrl = deviceCoreServiceUrl;
    }

    @Override public HeatingSystemDto getHeatingSystem(Long id) {
        HeatingSystem heatingSystem = getDevice(id);
        String url = deviceCoreServiceUrl + "/api/v1/devices/" + heatingSystem.getIotHubDeviceId() + "/state";
        ApiDeviceStateDto state = restTemplate.getForObject(url, ApiDeviceStateDto.class);
        if (state == null) throw new RuntimeException("Device has no status yet");
        return new HeatingSystemDto(
                heatingSystem.getId(),
                state.reportedState.isOn,
                state.reportedState.targetTemperature,
                state.telemetry.temperature
        );

    }

    //TODO не совсем понятно назначение этого метода
    // (тут в кучу и телеметрия и состояние для которые есть ниже методы отдельные)
    @Override public HeatingSystemDto updateHeatingSystem(Long id, HeatingSystemDto heatingSystemDto) {
        Map<String, Object> params = new HashMap<>();
        params.put("isOn", heatingSystemDto.getIsOn());
        params.put("targetTemperature", heatingSystemDto.getTargetTemperature());
        desireNewDeviceState(id, params);
        return getHeatingSystem(id);
    }


    @Override public void turnOn(Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("isOn", true);
        desireNewDeviceState(id, params);
    }


    @Override
    public void turnOff(Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("isOn", false);
        desireNewDeviceState(id, params);
    }

    @Override
    public void setTargetTemperature(Long id, double temperature) {
        Map<String, Object> params = new HashMap<>();
        params.put("targetTemperature", temperature);
        desireNewDeviceState(id, params);
    }

    @Override
    public Double getCurrentTemperature(Long id) {
        return getHeatingSystem(id).getCurrentTemperature();
    }

    private void desireNewDeviceState(Long id, Map<String, Object> params) {
        HeatingSystem device = getDevice(id);
        String url = String.format("%s/api/v1/devices/%s/state", deviceCoreServiceUrl, device.getIotHubDeviceId());
        restTemplate.postForEntity(url, params, Map.class);
    }

    private HeatingSystem getDevice(Long id) {
        return heatingSystemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
    }

    private static class ApiDeviceStateDto {
        public ReportedState reportedState;
        public Telemetry telemetry;
    }

    private static class ReportedState {
        public Boolean isOn;
        public Double targetTemperature;
    }

    private static class Telemetry {
        public Double temperature;
    }
}

