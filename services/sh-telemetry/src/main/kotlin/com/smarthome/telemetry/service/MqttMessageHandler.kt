package com.smarthome.telemetry.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.commons.logging.LogFactory
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory
import org.springframework.integration.mqtt.core.MqttPahoClientFactory
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter
import org.springframework.integration.mqtt.support.MqttHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.stereotype.Service

@Service
class MqttMessageHandler (val telemetryService: TelemetryService){
    private val logger = LogFactory.getLog(javaClass)

    @ServiceActivator(inputChannel = "mqttInputChannel")
    fun handleMessage(message: Message<String>) {
        logger.info("handleMessage: $message")
        try {
            val topic = message.headers[MqttHeaders.RECEIVED_TOPIC] as String
            val deviceId = topic.split("/")[1]
            val payload = jacksonObjectMapper().readValue<Map<String, Any>>(message.payload)
            telemetryService.processTelemetryMessage(deviceId, payload)
        }catch (e: Exception){
            logger.error("Processing error ${e}")
        }
    }
}



