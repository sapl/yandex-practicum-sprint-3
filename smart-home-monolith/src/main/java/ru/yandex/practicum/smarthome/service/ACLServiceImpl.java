package ru.yandex.practicum.smarthome.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.smarthome.dto.HeatingSystemDto;
import ru.yandex.practicum.smarthome.entity.HeatingSystem;
import ru.yandex.practicum.smarthome.repository.HeatingSystemRepository;

import java.util.HashMap;
import java.util.Map;


@Service
public class ACLServiceImpl implements ACLService {
    private static final Logger logger = LoggerFactory.getLogger(ACLServiceImpl.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate;
    private final String deviceCoreServiceUrl;
    private final HeatingSystemRepository heatingSystemRepository;
    private final HeatingSystemTCPConnector connector;

    private final MqttConfig.MqttGateway mqttGateway;


    // TODO если бы был реальный сервис
    //  - корректный проброс и обработка ошибок
    //  - кеширование
    //  - отдельный интерфейс для маппинга статуса и телеметрии
    //  - какая логика по регистрации новых устройств в Iot хабе
    //  - мы предполагаем что данные телеметрии уже мигрированы в новый сервис телеметрии

    @Autowired
    public ACLServiceImpl(
            HeatingSystemRepository heatingSystemRepository,
            RestTemplate restTemplate,
            HeatingSystemTCPConnector connector,
            MqttConfig.MqttGateway mqttGateway,
            @Value("${services.sh-device-core.url}") String deviceCoreServiceUrl) {
        this.restTemplate = restTemplate;
        this.connector = connector;
        this.mqttGateway = mqttGateway;
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
        restTemplate.postForObject(url, params, Map.class);
    }

    private HeatingSystem getDevice(Long id) {
        return heatingSystemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
    }


    @ServiceActivator(inputChannel = "mqttInputChannel")
    private void handleMessage(Message<String> message) {
        logger.info("handleMessage: {}", message);
        try {
            String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
            String[] topicParts = topic.split("/");
            String deviceId = topicParts[1];
            String channel = topicParts[2];
            Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);

            HeatingSystem system = heatingSystemRepository.findByIotHubDeviceId(deviceId);
            if (system != null)
                if (channel.equals("command")) {
                    connector.sendToDevice(system.getId(), payload);
                }
        } catch (Exception e) {
            logger.error("Processing error", e);
        }
    }

    @EventListener
    public void onTelemetryReceived(HeatingSystemTCPConnector.TelemetryEvent event) {
        try {
            HeatingSystem system = getDevice(event.systemId);
            String topic = "devices/" + system.getIotHubDeviceId() + "/telemetry";
            mqttGateway.sendToMqtt(
                    objectMapper.writeValueAsString(event.message),
                    topic);
            logger.info("Message published to topic=" + topic + ", message: " + event.message);
        } catch (Exception e) {
            logger.error("" + e);
        }
    }

    public void onTelemetryReceived(Long heatingSystemId, Map<String, Object> message) {
        try {
            HeatingSystem system = getDevice(heatingSystemId);
            mqttGateway.sendToMqtt("devices/" + system.getIotHubDeviceId() + "/telemetry",
                    objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            logger.error("" + e);
        }
    }

    public void onStateUpdated(Long heatingSystemId, Map<String, Object> message) {
        try {
            HeatingSystem system = getDevice(heatingSystemId);
            mqttGateway.sendToMqtt(objectMapper.writeValueAsString(message), "devices/" + system.getIotHubDeviceId() + "/state");
        } catch (JsonProcessingException e) {
            logger.error("" + e);
        }
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

