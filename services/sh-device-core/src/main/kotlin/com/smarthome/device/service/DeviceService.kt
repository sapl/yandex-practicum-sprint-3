package com.smarthome.device.service

import com.smarthome.device.config.NotFoundException
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
class DeviceService(val deviceStateRepository: DeviceStateRepository) {
    private val logger = LogFactory.getLog(javaClass)


    //TODO в реальном проекте
    //  - desire обновляет пользователь, reported - device
    //  - потоконезависимость при обновлении

    fun updateDesiredState(deviceId: String, update: Map<String, Any>) {
        updateDeviceState(deviceId, update, isDesired = true)
        Executors.newScheduledThreadPool(1).schedule({
            updateReportedState(deviceId, update)
        }, 5, TimeUnit.SECONDS)
    }

    fun updateReportedState(deviceId: String, update: Map<String, Any>) {
        updateDeviceState(deviceId, update, isDesired = false)
    }

    private fun updateDeviceState(deviceId: String, update: Map<String, Any>, isDesired: Boolean) {
        val state = deviceStateRepository.findByIdOrNull(deviceId) ?: DeviceState(deviceId = deviceId)
        state.lastOnlineAt = Instant.now()
        val targetState = (if (isDesired) state.desiredState else state.reportedState)
        if (!isDesired) {
            state.desiredState.clear()
            logger.info("Device [${deviceId}] state updated: ${update}")
        }
        targetState.putAll(update)
        targetState.put("timestamp", Instant.now())
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
        } ?: throw NotFoundException("No device found")


}