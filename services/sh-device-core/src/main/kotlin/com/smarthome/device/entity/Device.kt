package com.smarthome.device.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.Instant
import java.util.UUID

@Document(collection = "devices")
data class Device(
    @Id var id: String = ObjectId.get().toString(),
    val type: String,
    val model: String,
    @Indexed(unique = true)
    val serialNumber: String,
    val firmwareVersion: String,
    val hardwareVersion: String,
    @Indexed(unique = true)
    val authKey: String = UUID.randomUUID().toString(),
    val createdAt: Instant = Instant.now()
)


interface DeviceRepository : MongoRepository<Device, String> {
    fun findBySerialNumber(serialNumber: String): Device?
    fun findByType(type: String): List<Device>
}
