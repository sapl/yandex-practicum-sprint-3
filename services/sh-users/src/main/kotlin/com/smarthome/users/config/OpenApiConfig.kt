package com.smarthome.users.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class OpenApiConfig {
    @Bean
    fun customOpenAPI(buildProperties: BuildProperties): OpenAPI {
        val openApi =
            OpenAPI().info(
                Info()
                    .title(buildProperties.name.uppercase())
                    .version(buildProperties.version)
            )
        return openApi
    }
}

