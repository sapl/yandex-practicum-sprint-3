package com.smarthome.users.dto

import io.swagger.v3.oas.annotations.media.Schema


@Schema
data class UserHomeDto(
    @field:Schema(
        description = "Home ID.", required = true,
        example = "xxxx"
    )
    val id: String,


    @field:Schema(
        description = "User home display name.", required = true,
        example = "My home."
    )
    val name: String,


    @field:Schema(
        description = "The users is owner of the home.", required = true,
        example = "true"
    )
    val isOwner: Boolean,

    @field:Schema(
        description = "Assigned device list to the home.", required = true
    )
    val deviceIds: List<String>
)


@Schema
data class UserHomeCreateDto(
    @field:Schema(
        description = "User home display name.", required = true,
        example = "My home."
    )
    val name: String
)