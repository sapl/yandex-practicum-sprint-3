package com.smarthome.telemetry.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID


@Entity
@Table(name = "device_telemetry")
data class DeviceTelemetry(
    @Id val id: String = UUID.randomUUID().toString(),
    @Column(name = "device_id") val deviceId: String = "",
    @Column(name = "sensor_name") val sensorName: String = "",
    val datetime: Instant = Instant.ofEpochMilli(0),
    val value: Float = 0F
)

@Repository
interface DeviceTelemetryRepository : JpaRepository<DeviceTelemetry, String> {
    fun findByDeviceId(
        deviceId: String
    ): List<DeviceTelemetry>

    fun findByDeviceIdAndSensorNameAndDatetimeAfterOrderByDatetimeDesc(
        deviceId: String,
        sensorName: String,
        timeFrom: Instant,
        pageable: Pageable
    ): List<DeviceTelemetry>
}