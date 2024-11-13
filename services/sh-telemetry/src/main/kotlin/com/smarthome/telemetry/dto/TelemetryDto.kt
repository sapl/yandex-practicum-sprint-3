package com.smarthome.telemetry.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant



@Schema
data class TelemetryValueDto(
    @field:Schema(
        description = "Home ID.", required = true,
        example = "2024-11-08T15:34:13Z"
    )
    val datetime: Instant,


    @field:Schema(
        description = "Value", required = true,
        example = "22.31"
    )
    val value: Float
)