package com.smarthome.device.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.Instant


@JsonInclude(JsonInclude.Include.NON_NULL)
data class DeviceStateDto(
    val lastOnlineAt: Instant? = null,
    val firmwareVersion: String? = null,
    val desiredState: Map<String,Any> = emptyMap(),
    val reportedState:  Map<String,Any> = emptyMap(),
    val telemetry: Map<String,Any> = emptyMap(),
)
