package com.smarthome.device.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.Instant


@Document(collection = "device_state")
data class DeviceState(
    @Id var deviceId: String,
    var lastOnlineAt: Instant? = Instant.now(),
    val firmwareVersion: String? = null,
    val desiredState: MutableMap<String, Any> = mutableMapOf(),
    val reportedState: MutableMap<String, Any> = mutableMapOf(),
    val telemetry: MutableMap<String, Any> = mutableMapOf(),
    val updatedAt: Instant = Instant.now()
)


interface DeviceStateRepository : MongoRepository<DeviceState, String>
