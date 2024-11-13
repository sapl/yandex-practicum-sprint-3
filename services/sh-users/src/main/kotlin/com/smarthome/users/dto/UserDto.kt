package com.smarthome.users.dto

import com.smarthome.users.entity.User
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant


@Schema
data class UserDto(
    @field:Schema(
        description = "User ID.", required = true,
        example = "xxxx"
    )
    val id: String,


    @field:Schema(
        description = "User display name.", required = true,
        example = "Boris Britva"
    )
    val name: String,


    @field:Schema(
        description = "User email address.", required = true,
        example = "boris@gmail.com"
    )
    val email: String,

    @field:Schema(
        description = "User registration time.", required = true
    )
    val createdAt: Instant
)


fun User.toDto() =
    UserDto(
        id = id,
        name = name,
        email = email,
        createdAt = createdAt
    )

@Schema
data class UserCreateDto(
    @field:Schema(
        description = "User display name.", required = true,
        example = "Boris Britva"
    )
    val name: String,
    @field:Schema(
        description = "User email address.", required = true,
        example = "boris@gmail.com"
    )
    val email: String
)