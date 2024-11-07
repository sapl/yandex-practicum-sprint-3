package com.smarthome.users.controller

import com.smarthome.users.service.UserService
import com.smarthome.users.dto.UserHomeCreateDto
import com.smarthome.users.dto.UserHomeDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/users")
@Tag(name = "User Homes")
class UserHomesController(private val userService: UserService) {

    @PostMapping("{userId}/homes")
    @Operation(summary = "Create User Home")
    fun createHome(
        @PathVariable userId: String,
        @RequestBody home: UserHomeCreateDto
    ) = userService.createHome(userId, home.name)

    @GetMapping("{userId}/homes")
    @Operation(summary = "Get User Homes")
    fun getUserHomes(@PathVariable userId: String): List<UserHomeDto> = userService.getHomes(userId)


}