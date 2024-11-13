package com.smarthome.device.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.smarthome.device.config.MqttGateway
import com.smarthome.device.dto.DeviceStateDto
import com.smarthome.device.entity.DeviceState
import com.smarthome.device.entity.DeviceStateRepository
import org.apache.commons.logging.LogFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Service
class DeviceService(
    val deviceStateRepository: DeviceStateRepository,
    val mqttGateway: MqttGateway
) {
    private val logger = LogFactory.getLog(javaClass)

    private val objectMapper = ObjectMapper();

    //TODO в реальном проекте
    //  - desire обновляет пользователь, reported - device
    //  - потоконезависимость при обновлении


    fun updateDesiredState(deviceId: String, update: Map<String, Any>) {
        val state = deviceStateRepository.findByIdOrNull(deviceId) ?: DeviceState(deviceId = deviceId)
        state.lastOnlineAt = Instant.now()
        state.desiredState.putAll(update)
        state.desiredState.put("timestamp", Instant.now())
        deviceStateRepository.save(state)

        mqttGateway.sendToMqtt(objectMapper.writeValueAsString(update), "devices/${deviceId}/command")

        // emulate is device reported update
        Executors.newScheduledThreadPool(1).schedule({
            mqttGateway.sendToMqtt(objectMapper.writeValueAsString(update), "devices/${deviceId}/state")
        }, 10, TimeUnit.SECONDS)

    }

    fun updateReportedState(deviceId: String, update: Map<String, Any>) {
        val state = deviceStateRepository.findByIdOrNull(deviceId) ?: DeviceState(deviceId = deviceId)
        state.lastOnlineAt = Instant.now()
        state.reportedState.putAll(update)
        state.reportedState.put("timestamp", Instant.now())
        state.desiredState.clear()
        logger.info("Device [${deviceId}] state updated: ${state.reportedState}")
        deviceStateRepository.save(state)
    }


    fun updateDeviceTelemetry(deviceId: String, update: Map<String, Any>) {
        val state = deviceStateRepository.findByIdOrNull(deviceId) ?: DeviceState(deviceId = deviceId)
        state.lastOnlineAt = Instant.now()
        state.telemetry.putAll(update)
        state.telemetry.put("timestamp", Instant.now())
        deviceStateRepository.save(state)
    }

    fun getLastState(deviceId: String): DeviceStateDto =
        deviceStateRepository.findByIdOrNull(deviceId)?.let {
            DeviceStateDto(
                lastOnlineAt = it.lastOnlineAt,
                reportedState = it.reportedState,
                desiredState = it.desiredState,
                telemetry = it.telemetry
            )
        } ?: DeviceStateDto()


}