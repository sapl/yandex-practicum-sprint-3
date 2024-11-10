package com.smarthome.device.controller

import com.smarthome.device.config.DefaultErrorResponse
import com.smarthome.device.config.NotFoundException
import com.smarthome.device.dto.DeviceDto
import com.smarthome.device.dto.toDto
import com.smarthome.device.entity.Device
import com.smarthome.device.entity.DeviceRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.bson.types.ObjectId
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("api/v1/devices", produces = [MediaType.APPLICATION_JSON_VALUE])
@Tag(name = "Device Core")
class DeviceController(val deviceRepository: DeviceRepository) {

    @Operation(summary = "Get Device Info", description = "The device type information.")
    @GetMapping("{deviceId}")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successful retrieval of device information",
                content = [Content(schema = Schema(implementation = DeviceDto::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Device not found",
                content = [Content(schema = Schema(implementation = DefaultErrorResponse::class))]
            )
        ]
    )
    fun getDevice(
        @Parameter(description = "ID of the device") @PathVariable deviceId: String
    ): DeviceDto = (deviceRepository.findByIdOrNull(deviceId)
        ?: throw NotFoundException("Device not found")).toDto()

    @Operation(summary = "Emulate Device Registration")
    @PostMapping()
    fun registerDevice(
        @RequestBody dto: DeviceDto
    ): DeviceDto = (deviceRepository.save(
        Device(
            id = dto.id ?: ObjectId.get().toString(),
            type = dto.type, model = dto.model,
            serialNumber = dto.serialNumber,
            hardwareVersion = dto.hardwareVersion
        )
    ).toDto())
}