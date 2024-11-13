package ru.yandex.practicum.smarthome.service;


import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class HeatingSystemTCPConnector implements ApplicationEventPublisherAware {
    public static final int TELEMETRY_EMULATION_PERIOD_SEC = 30;
    private ApplicationEventPublisher eventPublisher;
    private static final Logger logger = LoggerFactory.getLogger(HeatingSystemTCPConnector.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    public void sendToDevice(long systemId, Map<String, Object> message) {
        logger.info("Message has been sent to systemId={} : {}", systemId, message);
        // отправка сообщения на устройство через какой-то протокол старый
    }

    public void handleTelemetryMessageFromDevice(long systemId, Map<String, Object> message) {
        logger.info("Message received from systemId={} : {}", systemId, message);
        //Какая-то старая логика обработки сообщений с устройства

        eventPublisher.publishEvent(new TelemetryEvent(this, systemId, message));
    }

    @PostConstruct
    public void scheduleTelemetryEmulation() {
        emulateTelemetryMessage();
        scheduler.scheduleAtFixedRate(this::emulateTelemetryMessage, 0, TELEMETRY_EMULATION_PERIOD_SEC, TimeUnit.SECONDS);
    }


    private void emulateTelemetryMessage() {
        Map<String, Object> message = new HashMap<>();
        BigDecimal temperature = BigDecimal.valueOf(21 + Math.random() * 3).setScale(2, RoundingMode.HALF_UP);
        message.put("temperature", temperature);
        handleTelemetryMessageFromDevice(1L, message);
    }

    @Override public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }

    public static class TelemetryEvent extends ApplicationEvent {
        public final long systemId;
        public final Map<String, Object> message;

        public TelemetryEvent(Object source, long systemId, Map<String, Object> message) {
            super(source);
            this.systemId = systemId;
            this.message = message;
        }
    }
}
