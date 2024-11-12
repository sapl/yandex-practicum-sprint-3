package com.smarthome.telemetry.service

import com.smarthome.telemetry.dto.TelemetryValueDto
import com.smarthome.telemetry.entity.DeviceTelemetry
import com.smarthome.telemetry.entity.DeviceTelemetryRepository
import jakarta.annotation.PreDestroy
import org.apache.commons.logging.LogFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

@Service
class TelemetryService(val telemetryRepository: DeviceTelemetryRepository) {
    private val buffer = LinkedBlockingQueue<DeviceTelemetry>()
    private val executor = Executors.newSingleThreadScheduledExecutor()
    private val maxBufferSize = 100
    private val saveInterval = 5L

    private val logger = LogFactory.getLog(javaClass)

    init {
        executor.scheduleAtFixedRate(this::saveTelemetry, saveInterval, saveInterval, TimeUnit.SECONDS)
    }


    //TODO в реальном проекте какой-то будет препроцессинг полей телеметрии
    fun processTelemetryMessage(deviceId: String, payload: Map<String, Any>) {
        val timestamp = parseTimestamp(payload["timestamp"]) ?: Instant.now()
        payload.forEach { (sensorName, value) ->
            val telemetryValue = when (value) {
                is Boolean -> if (value) 1F else 0F
                is Number -> value.toFloat()
                else -> return@forEach
            }
            buffer.add(
                DeviceTelemetry(
                    deviceId = deviceId,
                    sensorName = sensorName,
                    datetime = timestamp,
                    value = telemetryValue
                )
            )
        }
        if (buffer.size >= maxBufferSize) {
            saveTelemetry()
        }
    }

    private fun saveTelemetry() {
        if (buffer.isNotEmpty()) {
            logger.info("${buffer.size} values have been saved to telemetry")
            val dataToSave = mutableListOf<DeviceTelemetry>()
            buffer.drainTo(dataToSave)
            telemetryRepository.saveAll(dataToSave);
        }
    }

    fun getTelemetry(
        deviceId: String,
        sensorName: String,
        timeFrom: Instant,
        pageable: Pageable
    ): List<TelemetryValueDto> {
        return telemetryRepository.findByDeviceIdAndSensorNameAndDatetimeAfterOrderByDatetimeDesc(
            deviceId = deviceId, sensorName = sensorName, timeFrom = timeFrom, pageable = pageable
        ).map { TelemetryValueDto(it.datetime, it.value) }
    }

    private fun parseTimestamp(timestamp: Any?): Instant? {
        return try {
            if (timestamp is String) Instant.parse(timestamp) else null
        } catch (e: DateTimeParseException) {
            null
        }
    }

    @PreDestroy
    fun shutdown() {
        saveTelemetry()
        executor.shutdown()
    }
}