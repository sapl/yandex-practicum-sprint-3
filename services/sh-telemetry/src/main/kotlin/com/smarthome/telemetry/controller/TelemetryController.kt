package com.smarthome.telemetry.controller

import com.smarthome.telemetry.dto.TelemetryValueDto
import com.smarthome.telemetry.service.TelemetryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant


@RestController
@RequestMapping("api/v1/devices", produces = [MediaType.APPLICATION_JSON_VALUE])
@Tag(name = "Telemetry")
class TelemetryController(val telemetryService: TelemetryService) {

    @PostMapping("{deviceId}/telemetry")
    @Operation(
        summary = "Send telemetry data",
        description = "The method receives telemetry in JSON format: \n\n" +
            "```" +
            "{" +
            "   \"temperature\": 22.31," +
            "   \"humidity\": 55 " +
            "}" +
            "```"
    )
    fun postTelemetry(
        @Parameter(description = "ID of the device") @PathVariable deviceId: String,
        @Parameter(
            description = "Telemetry data",
            example = "{\"temperature\": 23.42}"
        ) @RequestBody payload: Map<String, Any>
    ) {
        telemetryService.processTelemetryMessage(deviceId, payload)
    }

    @GetMapping("{deviceId}/telemetry")
    @Operation(summary = "Get telemetry series")
    fun getTelemetry(
        @Parameter(description = "ID of the device") @PathVariable deviceId: String,
        @Parameter(description = "Sensor name", example = "temperature") @RequestParam sensorName: String,
        @Parameter(description = "The start time", example = "2024-11-08T13:14:13Z") @RequestParam timeFrom: Instant
    ): List<TelemetryValueDto> {
        return telemetryService.getTelemetry(deviceId, sensorName, timeFrom)
    }
}