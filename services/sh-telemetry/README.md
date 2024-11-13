# Data Processing Service

Сервис обработки и хранения телеметрии. Принимает потоки данных от устройств (из MQTT топиков), обрабатывает
складирует в Time-series базу (Clickhouse). Может генерировать дополнительные события в ходе обработки.
Предоставляет HTTP API доступа к данным и аггрегатам для аналитики и UI

```env

DB_CONNECTION_URL=jdbc:clickhouse://localhost:8123/default
MQTT_CONNECTION_URL=tcp://localhost:1883
```

### Ссылки

- Health check:
  http://localhost:8080/actuator/health

- Open API Doc UI:
  http://localhost:8080/redoc.html

- Open API Yaml:
  http://localhost:8080/v3/api-docs.yaml