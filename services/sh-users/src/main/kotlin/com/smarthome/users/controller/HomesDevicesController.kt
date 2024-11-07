package com.smarthome.users.controller

import com.smarthome.users.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/homes")
@Tag(name = "Home Devices")
class HomesDevicesController(private val userService: UserService) {

    @PutMapping("{homeId}/devices/{deviceId}")
    @Operation(summary = "Assign device to the home")
    fun assignDevice(
        @PathVariable deviceId: String, @PathVariable homeId: String
    ) = userService.assignDevice(homeId = homeId, deviceId = deviceId)

    @DeleteMapping("{homeId}/devices/{deviceId}")
    @Operation(summary = "Unassign device")
    fun unAssignDevice(
        @PathVariable deviceId: String, @PathVariable homeId: String
    ) = userService.unAssignDevice(deviceId = deviceId)


}