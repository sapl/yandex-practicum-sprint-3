package com.smarthome.users.config

import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.AbstractRequestLoggingFilter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


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
