package com.smarthome.device.config


import org.apache.commons.logging.LogFactory
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.MessagingGateway
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory
import org.springframework.integration.mqtt.core.MqttPahoClientFactory
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter
import org.springframework.integration.mqtt.support.MqttHeaders
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.MessageHandler
import org.springframework.messaging.handler.annotation.Header
import java.util.*

@Configuration
class MqttInboundConfig(@Value("\${mqtt.url}") val url: String,
                        @Value("\${mqtt.topic}") val topic: String) {
    private val logger = LogFactory.getLog(javaClass)

    @Bean
    fun mqttClientFactory(): MqttPahoClientFactory {
        logger.info("Initialising mqttClientFactory, mqtt url: ${url}")
        val factory = DefaultMqttPahoClientFactory()
        val options = MqttConnectOptions()
        options.isAutomaticReconnect = true
        options.keepAliveInterval = 60
        options.connectionTimeout = 10
        options.serverURIs = arrayOf(url)
        factory.connectionOptions = options
        return factory
    }

    @Bean
    fun mqttInputChannel(): MessageChannel = DirectChannel()

    @Bean
    fun mqttInbound(): MqttPahoMessageDrivenChannelAdapter {
        logger.info("Initialising mqttInbound, mqtt url: ${url}")
        return MqttPahoMessageDrivenChannelAdapter(
            "clientId-" + UUID.randomUUID(), mqttClientFactory(), topic
        ).apply {
            setCompletionTimeout(5000)
            setConverter(DefaultPahoMessageConverter())
            setQos(1)
            outputChannel = mqttInputChannel()
        }
    }

    @Bean
    fun mqttOutboundChannel(): MessageChannel {
        return DirectChannel()
    }
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    fun mqttOutbound(): MessageHandler {
        val messageHandler = MqttPahoMessageHandler("clientId-" + UUID.randomUUID(), mqttClientFactory())
        messageHandler.setAsync(true)
        messageHandler.setDefaultQos(1)
        return messageHandler
    }


}

@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
interface MqttGateway {
    fun sendToMqtt(data: String?, @Header(MqttHeaders.TOPIC) topic: String?)
}
