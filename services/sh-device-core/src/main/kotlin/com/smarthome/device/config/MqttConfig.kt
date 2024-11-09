package com.smarthome.device.config


import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory
import org.springframework.integration.mqtt.core.MqttPahoClientFactory
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter
import org.springframework.messaging.MessageChannel

@Configuration
class MqttInboundConfig(@Value("\${mqtt.url}") val url: String,
                        @Value("\${mqtt.topic}") val topic: String) {
    @Bean
    fun mqttClientFactory(): MqttPahoClientFactory {
        val factory = DefaultMqttPahoClientFactory()
        val options = MqttConnectOptions()
        options.serverURIs = arrayOf(url)
        factory.connectionOptions = options
        return factory
    }

    @Bean
    fun mqttInputChannel(): MessageChannel = DirectChannel()

    @Bean
    fun mqttInbound(): MqttPahoMessageDrivenChannelAdapter {
        return MqttPahoMessageDrivenChannelAdapter(
            "clientId", mqttClientFactory(), topic
        ).apply {
            setCompletionTimeout(5000)
            setConverter(DefaultPahoMessageConverter())
            setQos(1)
            outputChannel = mqttInputChannel()
        }
    }
}