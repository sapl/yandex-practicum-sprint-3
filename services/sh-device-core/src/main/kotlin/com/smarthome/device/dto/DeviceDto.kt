package com.smarthome.device.dto

import com.smarthome.device.entity.Device
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant


@Schema
data class DeviceDto(
    @field:[
    Schema(
        description = "Device unique ID.", required = true,
        example = "672d2e148e2e6a01c235e984"
    )]
    var id: String?,

    @field:Schema(
        description = "Device type.", required = true,
        example = "HEATING_SYSTEM"
    )
    val type: String,
    //
    @field:Schema(
        description = "Device type.", required = true,
        example = "Mi Smart Space Heater"
    )
    val model: String,
    //
    @field:Schema(
        description = "Device unique Serial Number.", required = true,
        example = "00-B0-D0-63-C2-26"
    )
    val serialNumber: String,
    //
    @field:Schema(
        description = "Device hardware version.", required = true,
        example = "BHR4037GL"
    )
    val hardwareVersion: String,

    //
    @field:Schema(
        description = "Device registration date.", required = true,
        example = "2024-11-08T20:26:33.880+00:00"
    )
    val createdAt: Instant = Instant.now()
)

fun Device.toDto() =
    DeviceDto(
        id = id,
        type = type,
        model = model,
        serialNumber = serialNumber,
        hardwareVersion = hardwareVersion,
        createdAt = createdAt
    )
