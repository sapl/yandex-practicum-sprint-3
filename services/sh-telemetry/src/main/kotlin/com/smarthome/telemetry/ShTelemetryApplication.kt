package com.smarthome.telemetry

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ShTelemetryApplication

fun main(args: Array<String>) {
    runApplication<ShTelemetryApplication>(*args)
}
