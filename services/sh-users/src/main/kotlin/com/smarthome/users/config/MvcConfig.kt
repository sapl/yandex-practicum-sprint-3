package com.smarthome.users.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.AbstractRequestLoggingFilter
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
import java.util.*


@Configuration
class SwaggerConfig {
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


@Configuration
class WebMvcConfig : WebMvcConfigurer {
    @Bean
    fun requestLoggingFilter(): RequestLoggingFilter = RequestLoggingFilter()
}
class RequestLoggingFilter : AbstractRequestLoggingFilter() {
    init {
        isIncludeQueryString = true
        isIncludePayload = true
        isIncludeHeaders = false
        isIncludeClientInfo = true
        setAfterMessagePrefix("[")
    }

    override fun shouldLog(request: HttpServletRequest): Boolean = true

    override fun beforeRequest(request: HttpServletRequest, message: String) = Unit

    override fun afterRequest(request: HttpServletRequest, message: String) {
        logger.info(message)
    }
}
