package com.smarthome.users.controller

import com.smarthome.users.service.UserService
import com.smarthome.users.dto.UserCreateDto
import com.smarthome.users.dto.UserDto
import com.smarthome.users.dto.toDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/users", produces = [MediaType.APPLICATION_JSON_VALUE])
@Tag(name = "Users")
class UserController(private val userService: UserService) {

    @PostMapping()
    @Operation(summary = "Create User")
    fun createUser(
        @RequestBody createDto: UserCreateDto
    ): UserDto = userService.createUser(createDto.name, createDto.email).toDto()


}