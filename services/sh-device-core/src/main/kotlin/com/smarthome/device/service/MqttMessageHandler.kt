package com.smarthome.device.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.commons.logging.LogFactory
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.mqtt.support.MqttHeaders
import org.springframework.messaging.Message
import org.springframework.stereotype.Service


@Service
class MqttMessageHandler(val deviceService: DeviceService) {
    private val logger = LogFactory.getLog(javaClass)

    @ServiceActivator(inputChannel = "mqttInputChannel")
    fun handleMessage(message: Message<String>) {
        logger.info("handleMessage: $message")
        try {
            val topic = message.headers[MqttHeaders.RECEIVED_TOPIC] as String
            val deviceId = topic.split("/")[1]
            val channel = topic.split("/")[2]
            val payload = jacksonObjectMapper().readValue<Map<String, Any>>(message.payload)
            when (channel) {
                "command" -> deviceService.updateDesiredState(deviceId, payload)
                "state" -> deviceService.updateReportedState(deviceId, payload)
                "telemetry" -> deviceService.updateDeviceTelemetry(deviceId, payload)
                else -> logger.error("Unsupported topic ${topic}")
            }

        } catch (e: Exception) {
            logger.error("Processing error ${e}")
        }
    }
}



