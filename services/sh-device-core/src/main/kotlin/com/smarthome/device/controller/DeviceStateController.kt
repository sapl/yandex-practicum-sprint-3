package com.smarthome.device.controller

import com.smarthome.device.dto.DeviceStateDto
import com.smarthome.device.service.DeviceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("api/v1/devices/{deviceId}/state", produces = [MediaType.APPLICATION_JSON_VALUE])
@Tag(name = "Device State")
class DeviceStateController(val deviceService: DeviceService) {

    @PostMapping()
    @Operation(
        summary = "Update desired state",
        description = "Update of the device desired state/settings, any JSON format: \n\n" +
            "```" +
            "{" +
            "   \"on\": true," +
            "   \"brightness\": 75 " +
            "}" +
            "```"
    )
    fun updateState(
        @Parameter(description = "ID of the device") @PathVariable deviceId: String,
        @RequestBody state: Map<String, Any>
    ) {
        deviceService.updateDesiredState(deviceId, state)
    }

    @Operation(summary = "Get last device state")
    @GetMapping
    fun getState(
        @Parameter(description = "ID of the device") @PathVariable deviceId: String
    ): DeviceStateDto = deviceService.getLastState(deviceId)
}