package com.smarthome.device.config

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Bad request error")
class BadRequestException(message: String) : RuntimeException(message, null)

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Not found")
class NotFoundException(message: String? = null) : RuntimeException(message ?: "Not found", null)

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Unauthorized")
class UnauthorizedException() : RuntimeException("Unauthorized", null)

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Forbidden")
class ForbiddenException(message: String? = null) : RuntimeException(message ?: "Forbidden", null)

data class DefaultErrorResponse(
    val status: Int,
    val error: String
)